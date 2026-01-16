package net.buildtheearth.buildteamtools.modules.generator.components.kml;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.cryptomorin.xseries.XMaterial;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.utils.BlockLocation;
import net.buildtheearth.buildteamtools.utils.GeometricUtils;
import net.buildtheearth.buildteamtools.utils.LineRasterization;
import net.buildtheearth.buildteamtools.utils.PolygonTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Class to handle kml command and its aliases<br><br>
 * <p>
 * Since kml content easily exceeds the normal chat/character limit,
 * we use the following workflow:<br>
 * 1) If sent from a player, generates a CommandBlock and adds some metadata to it
 * the player then pastes the kml contents and confirms the command<br>
 * 2) The CommandBlock then sends a kml command with the contents, which is parsed by the server<br><br>
 * <p>
 * note: this command differentiates between aliases to determine wether to
 * generate intermediate points between the geo-coordinates from the kml linestrings.
 * Players should only use /geopoints or /geopath aliases,
 * direct use of the /kml command is restricted to CommandBlockSender
 *
 */
public class KmlCommand implements CommandExecutor {

    /**
     * Handles the execution of KML-related commands (/geopoints, /geopath, /georing, /kml).
     * <p>
     * This method has two main execution paths:
     * <ul>
     *   <li>If sender is a CommandBlock: directly processes the KML content</li>
     *   <li>If sender is a Player: creates a CommandBlock UI for pasting KML content or handles undo</li>
     * </ul>
     *
     * @param sender The command sender (Player or CommandBlock)
     * @param cmd    The command object
     * @param alias  The alias used to invoke this command (geopoints/geopath/georing/kml)
     * @param args   Command arguments (KML content if from CommandBlock, block type if from Player)
     * @return true if the command was handled successfully, false otherwise
     */
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String alias, String @NonNull [] args) {
        if (sender instanceof BlockCommandSender cmdbSender) {
            Block senderBlock = cmdbSender.getBlock();

            return processKml(senderBlock, args);
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cOnly players can execute this command.");
            return false;
        }

        if (args.length > 0 && args[0].equals("undo")) {
            return undoCommand(p);
        }

        //check if alias is geopoints or geopath (direct /kml is only allowed for undo)
        if (alias.equals("kml")) {
            sender.sendMessage("§cPlease use /geopoints or /geopath to execute this command.");
            return false;
        }

        return createPasteUI(p, cmd, alias, args);
    }

    /**
     * Adds a single block location to the transaction, optionally extending down to terrain.
     * <p>
     * If extendToGround is enabled, this will also fill all blocks vertically from the terrain
     * elevation up to the specified location height.
     *
     * @param loc            The block location to add
     * @param extendToGround Whether to fill blocks down to terrain level
     * @param world          The world containing the blocks
     * @param container      The set to add the block location to
     * @param fillPositions  The set to add vertical fill positions to
     */
    void addSingleLocation(BlockLocation loc, boolean extendToGround, World world, @NonNull Set<BlockLocation> container, Set<BlockLocation> fillPositions) {
        container.add(loc);
        if (extendToGround) fillVerticalToTerrain(loc, world, fillPositions);
    }

    /**
     * Adds multiple block locations to the transaction, optionally extending each down to terrain.
     * <p>
     * This is a convenience method that calls {@link #addSingleLocation(BlockLocation, boolean, World, Set, Set)}
     * for each location in the list.
     *
     * @param locs           List of block locations to add
     * @param extendToGround Whether to fill blocks down to terrain level for each location
     * @param world          The world containing the blocks
     * @param container      The set to add the block locations to
     * @param fillPositions  The set to add vertical fill positions to
     */
    void addLocations(@NonNull List<BlockLocation> locs, boolean extendToGround, World world, Set<BlockLocation> container, Set<BlockLocation> fillPositions) {
        for (BlockLocation loc : locs)
            addSingleLocation(loc, extendToGround, world, container, fillPositions);
    }

    /**
     * Fills all block positions vertically from terrain level up to the specified location.
     * <p>
     * This is used when the "extend to ground" feature is enabled, creating a vertical
     * column of blocks from the highest block at the XZ coordinates up to the target height.
     *
     * @param locationOverGround The target block location (height to extend to)
     * @param world              The world to query for terrain height
     * @param container          The set to add the vertical fill positions to
     */
    void fillVerticalToTerrain(BlockLocation locationOverGround, @NonNull World world, Set<BlockLocation> container) {
        int terrainElevation = world.getHighestBlockYAt(locationOverGround.x, locationOverGround.z);
        for (int y = terrainElevation; y < locationOverGround.y; y++) {
            container.add(new BlockLocation(locationOverGround.x, y, locationOverGround.z));
        }
    }

    /**
     * Processes KML content from a CommandBlock and places blocks in the world.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Reads metadata from the command block (player, block type, operation mode)</li>
     *   <li>Parses the KML content to extract geographic coordinates</li>
     *   <li>Converts geographic coordinates to Minecraft coordinates (async)</li>
     *   <li>Rasterizes lines/points based on the operation mode</li>
     *   <li>Filters blocks by distance and chunk loading</li>
     *   <li>Commits block changes to the world (sync)</li>
     *   <li>Cleans up the command block</li>
     * </ol>
     * <p>
     * The coordinate conversion and processing is done asynchronously to avoid blocking
     * the server thread, then syncs back to the main thread for actual block changes.
     * <p>
     * Blocks are only placed if:
     * <ul>
     *   <li>The chunk is loaded</li>
     *   <li>The block is within 1000 blocks of the player</li>
     * </ul>
     *
     * @param senderBlock The CommandBlock that sent this command (contains metadata)
     * @param args        The KML content as a string array
     * @return true if processing started successfully, false if validation failed
     */
    public boolean processKml(@NonNull Block senderBlock, String[] args) {

        //read metadata to get player name/ID
        BlockState blockState = senderBlock.getState();
        String playerName = blockState.getMetadata("kmlPlayerName").getFirst().asString();
        String blocktypeString = blockState.getMetadata("kmlBlocktype").getFirst().asString();
        String previousCommandBlockType = blockState.getMetadata("kmlPreviousBlocktype").getFirst().asString();
        String blockCreationCommand = blockState.getMetadata("kmlBlockCreationCommand").getFirst().asString();
        BlockCreationMode operationMode = commandToCreationMode(blockCreationCommand);

        boolean extendToGround = blockState.getMetadata("kmlExtendToGround").getFirst().asBoolean();
        String extendToGroundBlockType = blockState.getMetadata("kmlExtendToGroundBlocktype").getFirst().asString();


        if (playerName.isEmpty() || blocktypeString.isEmpty()) {
            //invalid metadata, cancel
            //send error message to all players within 50m of the command block
            ChatHelper.sendMessageToPlayersNearLocation(senderBlock.getLocation(), "§cReceived /kml command from CommandBlock without sufficient metadata.\nThis command can only be executed from a CommandBlock created with the /kml command!", 50);
            return false;
        }

        Player player = Bukkit.getServer().getPlayer(playerName);

        if (player == null) {
            return false;
        }

        String kml_content = String.join(" ", args);

        Material matchedBlockMaterial = Material.matchMaterial(blocktypeString);
        final Material blockMaterial = (matchedBlockMaterial != null && matchedBlockMaterial.isBlock()) ? matchedBlockMaterial : Material.BRICKS;
        if (matchedBlockMaterial == null || !matchedBlockMaterial.isBlock()) {
            player.sendMessage("§cServer received /kml command with invalid blocktype string metadata. Using bricks as fallback.");
        }

        Material matchedExtendMaterial = Material.matchMaterial(extendToGroundBlockType);
        final Material extendMaterial = (matchedExtendMaterial != null && matchedExtendMaterial.isBlock()) ? matchedExtendMaterial : Material.BRICKS;
        if (matchedExtendMaterial == null || !matchedExtendMaterial.isBlock()) {
            player.sendMessage("§cServer received /kml command with invalid blocktype string metadata. Using bricks as fallback.");
        }

        //parse kml
        long time_beforeKMLParse = System.currentTimeMillis();

        KmlParser parser = new KmlParser(player); // we pass the player here to be able to report parsing errors
        List<List<Coordinate>> geoCoords = parser.extractCoordinates(kml_content);
        long time_afterKMLParse = System.currentTimeMillis();

        World world = senderBlock.getWorld();

        // Run the coordinate conversion asynchronously to avoid blocking the server thread
        Bukkit.getScheduler().runTaskAsynchronously(BuildTeamTools.getInstance(), () -> {
            List<List<Location>> mcLocations = convertToMC(geoCoords, world);
            long time_afterProjection = System.currentTimeMillis();


            //set up a transaction (collection of block changes)
            ChangeTransaction transaction = new ChangeTransaction(player);

            //collect all blocklocations in a set
            //if just iterate and create blocks one by one, we create multiple blocks at the same XZ coordinates,
            // this also stacks them vertically because we check terrain altitude.
            Set<BlockLocation> blockPositions = new HashSet<>();
            Set<BlockLocation> fillPositions = new HashSet<>();


            for (List<Location> polyline : mcLocations) {
                if (operationMode == BlockCreationMode.FILLED) blockPositions = triangulateAndFill(polyline);
                else {
                    //rasterize line and create intermediate blocks
                    //note: iteration starts at second block, so we always have a previous block to draw the line
                    //for single point mode, we explicitly add the first block
                    if (operationMode == BlockCreationMode.POINTS) {
                        BlockLocation loc = new BlockLocation(polyline.getFirst());
                        addSingleLocation(loc, extendToGround, world, blockPositions, fillPositions);
                    }

                    // For PATH/CLOSED_PATH modes, start from the first point
                    int startIndex = (operationMode == BlockCreationMode.POINTS) ? 1 : 0;

                    for (int i = startIndex + 1; i < polyline.size(); ++i) {
                        if (operationMode == BlockCreationMode.POINTS) {
                            BlockLocation loc = new BlockLocation(polyline.get(i));
                            addSingleLocation(loc, extendToGround, world, blockPositions, fillPositions);
                        } else //interpolate
                        {
                            addLocations(LineRasterization.rasterizeLine(polyline.get(i - 1), polyline.get(i)), extendToGround, world, blockPositions, fillPositions);
                        }
                    }

                    //for closed-path-mode, add extra line between start and end
                    if (operationMode == BlockCreationMode.CLOSED_PATH) {
                        addLocations(LineRasterization.rasterizeLine(polyline.getFirst(), polyline.getLast()), extendToGround, world, blockPositions, fillPositions);
                    }
                }
            }

            //now create the blocks
            // restrictions: only if block is loaded, and only within 1000 blocks of the current player location
            final boolean[] preventedUnloadedChunkChanges = {false};
            final boolean[] preventedFarChanges = {false};

            float maxDistanceToPlayer = 1000; //TODO server config

            for (BlockLocation pt : blockPositions) {
                Location loc = pt.getLocation(world);
                if (!loc.getChunk().isLoaded()) preventedUnloadedChunkChanges[0] = true;
                else if (loc.distance(player.getLocation()) > maxDistanceToPlayer) preventedFarChanges[0] = true;
                else {
                    transaction.addBlockChange(pt, world, blockMaterial);
                }
            }

            for (BlockLocation pt : fillPositions) {
                Location loc = pt.getLocation(world);
                if (!loc.getChunk().isLoaded()) preventedUnloadedChunkChanges[0] = true;
                else if (loc.distance(player.getLocation()) > maxDistanceToPlayer) preventedFarChanges[0] = true;
                else {
                    transaction.addBlockChange(pt, world, extendMaterial);
                }
            }


            if (transaction.size() == 0) {
                StringBuilder debugMessage = new StringBuilder("§ckml command did not contain any allowed block changes. ");
                debugMessage.append("Total positions calculated: ").append(blockPositions.size() + fillPositions.size()).append(". ");
                debugMessage.append("Player at: X=").append(player.getLocation().getBlockX());
                debugMessage.append(" Y=").append(player.getLocation().getBlockY());
                debugMessage.append(" Z=").append(player.getLocation().getBlockZ()).append(". ");

                // Show first calculated block location for debugging
                if (!blockPositions.isEmpty()) {
                    BlockLocation firstBlock = blockPositions.iterator().next();
                    debugMessage.append("First block would be at: X=").append(firstBlock.x);
                    debugMessage.append(" Y=").append(firstBlock.y);
                    debugMessage.append(" Z=").append(firstBlock.z).append(". ");
                }

                if (preventedUnloadedChunkChanges[0]) {
                    debugMessage.append("Some blocks are in unloaded chunks. ");
                }
                if (preventedFarChanges[0]) {
                    debugMessage.append("Some blocks are >1000 blocks from your location (max distance 1000). ");
                }
                if (!preventedUnloadedChunkChanges[0] && !preventedFarChanges[0]) {
                    debugMessage.append("No positions were calculated from the KML. Check your coordinates and alias (/geopoints, /geopath, /georing).");
                }
                player.sendMessage(debugMessage.toString());
                return;
            }

            // Sync back to main thread for block changes and player history management
            Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), () -> {
                //create commandHistory if not exits
                if (KmlCommand.this.playerHistories == null) KmlCommand.this.playerHistories = new HashMap<>();

                if (!KmlCommand.this.playerHistories.containsKey(player))
                    KmlCommand.this.playerHistories.put(player, new Stack<>());

                Stack<ChangeTransaction> playerHistory = KmlCommand.this.playerHistories.get(player);

                playerHistory.push(transaction);


                long time_beforeBlockChange = System.currentTimeMillis();


                player.sendMessage(String.format("KML parsing: %d ms. BTE Projection: %d ms, Transaction preparation: %d ms. Changing %d blocks, please stand by.", (time_afterKMLParse - time_beforeKMLParse), (time_afterProjection - time_afterKMLParse), (time_beforeBlockChange - time_afterProjection), transaction.size()));

                int blocksChanged = transaction.commit();

                long time_afterBlockChanged = System.currentTimeMillis();
                player.sendMessage(String.format("KML command changed %d blocks (%d ms).", blocksChanged, (time_afterBlockChanged - time_beforeBlockChange)));


                if (preventedUnloadedChunkChanges[0]) {
                    player.sendMessage("§cSome block changes target unloaded chunks and were not applied.");
                }
                if (preventedFarChanges[0]) {
                    player.sendMessage("§cSome block changes target blocks too far away from the player and were not applied.");
                }

                Material material = Material.matchMaterial(previousCommandBlockType);

                if (material == null) {
                    player.sendMessage("§cServer received /kml command with invalid blocktype string metadata. Using bricks as fallback.");
                    material = Material.BRICKS;
                }

                //Delete command block, restore previous type
                senderBlock.setType(material);
            });
        });
        return true;
    }

    /**
     * Triangulates a polygon defined by a polyline and fills it with blocks.
     * <p>
     * This method:
     * <ol>
     *   <li>Triangulates the polygon into triangles using {@link PolygonTools#triangulatePolygon(List)}</li>
     *   <li>Rasterizes each triangle to fill it with blocks</li>
     *   <li>Also adds triangle borders for debugging purposes</li>
     * </ol>
     *
     * @param polyline The list of locations defining the polygon boundary
     * @return A set of all block locations that should be filled
     */
    private @NonNull Set<BlockLocation> triangulateAndFill(List<Location> polyline) {
        Set<BlockLocation> result = new HashSet<>();
        //triangulate the polygon
        List<PolygonTools.Triangle> triangles = PolygonTools.triangulatePolygon(polyline);
        //fill with bresenham. for now, just all border-lines
        for (PolygonTools.Triangle tri : triangles) {
            result.addAll(PolygonTools.rasterizeTriangle(tri));

            //dEBUG: ALL triangle borders
            result.addAll(LineRasterization.rasterizeLine(tri.getVertex1(), tri.getVertex2()));
            result.addAll(LineRasterization.rasterizeLine(tri.getVertex1(), tri.getVertex3()));
            result.addAll(LineRasterization.rasterizeLine(tri.getVertex2(), tri.getVertex3()));
        }
        return result;
    }


    /**
     * Creates a CommandBlock UI for pasting KML content.
     * <p>
     * This method:
     * <ol>
     *   <li>Parses command arguments for block type and extend options</li>
     *   <li>Spawns a CommandBlock 2 blocks in front of the player</li>
     *   <li>Stores metadata on the CommandBlock (player, block types, operation mode)</li>
     *   <li>Configures the CommandBlock to execute /kml when triggered</li>
     * </ol>
     * <p>
     * Arguments:
     * <ul>
     *   <li>Optional block type (e.g., "STONE", "GLASS") - defaults to "BRICKS"</li>
     *   <li>Optional -extend:BLOCKTYPE to fill vertically down to terrain</li>
     * </ul>
     * <p>
     * After creation, the player should:
     * <ol>
     *   <li>Right-click the CommandBlock</li>
     *   <li>Paste the KML content after "/kml "</li>
     *   <li>Set the CommandBlock to "Always Active"</li>
     *   <li>Confirm to execute</li>
     * </ol>
     *
     * @param player The player creating the CommandBlock UI
     * @param cmd    The command object (unused)
     * @param alias  The command alias used (determines operation mode)
     * @param args   Command arguments (block type and extend options)
     * @return true if the CommandBlock was created successfully
     */
    public boolean createPasteUI(Player player, Command cmd, String alias, String @NonNull [] args) {
        //The command either creates a command-block at the player location
        //arguments are an 
        //  optional blocktype 
        //  optional -toGround:blockType
        String blocktype = "BRICKS";
        boolean extendToGround = false;
        String extendToGroundBlockType = "GREEN_WOOL";
        String prefix_extendParam = "-extend:";
        for (String arg : args) {
            if (arg.startsWith(prefix_extendParam)) {
                extendToGround = true;
                extendToGroundBlockType = arg.substring(prefix_extendParam.length()).toUpperCase();

                if (Material.matchMaterial(extendToGroundBlockType) == null) {
                    player.sendMessage(String.format("§cInvalid block type for extend parameter '%s'. Using bricks as fallback.", extendToGroundBlockType));
                    extendToGroundBlockType = "BRICKS";
                }
            } else {
                blocktype = arg.toUpperCase();

                if (Material.matchMaterial(blocktype) == null) {
                    player.sendMessage(String.format("§cInvalid block type '%s'. Using bricks as fallback.", blocktype));
                    blocktype = "BRICKS";
                }
            }
        }

        //spawn a command block in front of the player
        Location commandBlockLocation = player.getLocation().add(player.getLocation().getDirection().multiply(2));
        commandBlockLocation = commandBlockLocation.add(0, 2, 0);

        World world = commandBlockLocation.getWorld();
        Block block = world.getBlockAt(commandBlockLocation);

        Material previousMaterial = block.getType(); //remember old blocktype to replace after command processing

        if (XMaterial.COMMAND_BLOCK.get() != null) block.setType(XMaterial.COMMAND_BLOCK.get());

        //for now, user has to manually set the command to "auto" to get it immediately triggered on confirm
        CommandBlock cmdBlock = (CommandBlock) block.getState();

        cmdBlock.setCommand("/kml "); //ready to paste kml content
        cmdBlock.setMetadata("kmlPlayerName", new FixedMetadataValue(BuildTeamTools.getInstance(), player.getName()));
        //cmdBlock.setMetadata("kmlPlayerID", new FixedMetadataValue(BuildTeamTools.getInstance(), p.getUniqueId()));
        cmdBlock.setMetadata("kmlBlocktype", new FixedMetadataValue(BuildTeamTools.getInstance(), blocktype));
        cmdBlock.setMetadata("kmlExtendToGround", new FixedMetadataValue(BuildTeamTools.getInstance(), extendToGround));
        cmdBlock.setMetadata("kmlExtendToGroundBlocktype", new FixedMetadataValue(BuildTeamTools.getInstance(), extendToGroundBlockType));

        cmdBlock.setMetadata("kmlPreviousBlocktype", new FixedMetadataValue(BuildTeamTools.getInstance(), previousMaterial.toString()));

        cmdBlock.setMetadata("kmlBlockCreationCommand", new FixedMetadataValue(BuildTeamTools.getInstance(), alias));

        cmdBlock.update();
        player.sendMessage("§6Command block created. Right click the block, paste the KML content, set it to 'always on' and confirm");
        return true;

    }

    /**
     * Undoes the last KML command executed by the player.
     * <p>
     * This method retrieves the most recent {@link ChangeTransaction} from the player's
     * history and reverses all block changes made by that transaction.
     * <p>
     * The undo history is maintained per-player in a stack, so multiple undos can be
     * performed in sequence to revert multiple KML commands.
     *
     * @param player The player requesting the undo
     * @return true if undo was successful, false if no history exists
     */
    public boolean undoCommand(Player player) {
        if (playerHistories == null) {
            player.sendMessage("kml undo failed - no command history available.");
            return false;
        }

        Stack<ChangeTransaction> playerHistory = playerHistories.get(player);

        if (playerHistory == null || playerHistory.empty()) {
            player.sendMessage("kml undo failed - there is no previously executed kml command.");
            return false;
        }

        ChangeTransaction transaction = playerHistory.pop();
        transaction.undo();
        player.sendMessage(String.format("undo successful. Restored %d blocks.", transaction.size()));
        return true;
    }

    /**
     * Converts geographic coordinates (latitude/longitude) to a Minecraft Location.
     * <p>
     * This method:
     * <ol>
     *   <li>Converts lat/lon to Minecraft X/Z using {@link GeometricUtils#getLocationFromCoordinates(double[])}</li>
     *   <li>Applies the KML altitude offset (Google Earth altitudes are relative to ground)</li>
     * </ol>
     * <p>
     * Note: GeometricUtils already adds +2 to terrain elevation, so we don't subtract it here anymore.
     *
     * @param coordinates     Array of [latitude, longitude] in degrees
     * @param altitudeFromKML Altitude from the KML file (relative to ground level)
     * @return A Bukkit Location with X/Z from projection and Y adjusted for altitude
     */
    private @NonNull Location getLocationFromCoordinates(double[] coordinates, double altitudeFromKML) {
        Location mcLocation = GeometricUtils.getLocationFromCoordinates(coordinates);
        //add altitude from kml (altitude from Google Earth is always relative to ground)
        //note: the "-2" is only neccesary because 
        //  getLocationFromCoordinates returns terrain altitude + 2
        //      (one from Bukkits getHighestBlockY and one from our geoutils)
        mcLocation.add(0, altitudeFromKML, 0);
        return mcLocation;
    }

    /**
     * Converts multiple lists of geographic coordinates to Minecraft Locations.
     * <p>
     * This method parallelizes the coordinate conversion using a thread pool to improve
     * performance when processing large KML files with many coordinates. Each coordinate
     * conversion is executed asynchronously, and the method waits for all conversions
     * to complete before returning.
     * <p>
     * For each list of coordinates (representing a polyline), it:
     * <ol>
     *   <li>Creates a thread pool sized to the number of coordinates</li>
     *   <li>Submits each coordinate conversion as a CompletableFuture</li>
     *   <li>Waits for all futures to complete using {@link CompletableFuture#join()}</li>
     *   <li>Collects the results into a list of Locations</li>
     * </ol>
     * <p>
     * Note: This method is called from an async task, so the .join() call won't block
     * the main server thread.
     *
     * @param geocoords_lists List of polylines, where each polyline is a list of KML Coordinates
     * @param world           The world context (currently unused but available for future use)
     * @return List of polylines converted to Minecraft Locations
     */
    private @NonNull List<List<org.bukkit.Location>> convertToMC(@NonNull List<List<Coordinate>> geocoords_lists, World world) {

        List<List<org.bukkit.Location>> mcLines = new ArrayList<>();

        //This lat/long to xyz conversion takes most of the runtime for this command
        // since the conversions do not influence each other, we can parallelize them.
        for (List<Coordinate> geocoords : geocoords_lists) {
            // ExecutorService to manage the threads
            try (ExecutorService executorService = Executors.newFixedThreadPool(geocoords.size())) {

                // List to store CompletableFuture results
                List<CompletableFuture<Location>> completableFutureList = new ArrayList<>();

                // Create a CompletableFuture for each set of coordinates
                for (Coordinate geocoord : geocoords) {
                    double[] coordinates = new double[]{geocoord.getLatitude(), geocoord.getLongitude()};

                    CompletableFuture<Location> completableFuture = CompletableFuture.supplyAsync(() -> getLocationFromCoordinates(coordinates, geocoord.getAltitude()), executorService);
                    completableFutureList.add(completableFuture);
                }

                // Combine all CompletableFutures into a single CompletableFuture representing all of them
                CompletableFuture<Void> allOf = CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0]));

                // Wait for all CompletableFutures to complete
                allOf.join();

                // Collect the results from the CompletableFutures
                List<Location> mcLine = completableFutureList.stream().map(CompletableFuture::join).toList();

                mcLines.add(mcLine);

                // Shutdown the ExecutorService
                executorService.shutdown();
            }
        }
        return mcLines;
    }

    /**
     * Maps command aliases to their corresponding block creation modes.
     * <p>
     * This method determines how KML coordinates should be processed:
     * <ul>
     *   <li><b>geopoints</b> → {@link BlockCreationMode#POINTS}: Places blocks only at coordinate points</li>
     *   <li><b>geopath</b> → {@link BlockCreationMode#PATH}: Draws lines between consecutive points</li>
     *   <li><b>georing</b> → {@link BlockCreationMode#CLOSED_PATH}: Draws lines and closes the path (connects last to first)</li>
     *   <li><b>geosurface</b> → {@link BlockCreationMode#FILLED}: Triangulates and fills the polygon (not yet implemented)</li>
     * </ul>
     *
     * @param command The command alias used to invoke the KML command
     * @return The BlockCreationMode corresponding to the command
     * @throws UnsupportedOperationException if command is "geosurface" (not implemented)
     * @throws IllegalArgumentException      if command is not recognized
     */
    private BlockCreationMode commandToCreationMode(@NonNull String command) {
        return switch (command) {
            case "geopoints" -> BlockCreationMode.POINTS;
            case "geopath" -> BlockCreationMode.PATH;
            case "georing" -> BlockCreationMode.CLOSED_PATH;
            case "geosurface" ->
                    throw new UnsupportedOperationException("Operation mode 'filled surface' is not yet implemented");
            //return BlockCreationMode.FILLED;
            default -> throw new IllegalArgumentException(command);
        };

    }

    /**
     * Per-player history of KML command transactions for undo functionality.
     * <p>
     * Each player has a stack of {@link ChangeTransaction} objects representing their
     * executed KML commands. This allows players to undo their changes using /kml undo.
     * <p>
     * The most recent transaction is at the top of the stack and will be undone first.
     */
    private HashMap<Player, Stack<ChangeTransaction>> playerHistories;
}
