package net.buildtheearth.buildteamtools.modules.generator.components.kml;

import com.cryptomorin.xseries.XMaterial;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.common.commands.BttCommandManager;
import net.buildtheearth.buildteamtools.modules.common.metrics.FeatureUsage;
import net.buildtheearth.buildteamtools.modules.common.metrics.UsageDetail;
import net.buildtheearth.buildteamtools.modules.common.metrics.UsageFeature;
import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.utils.BlockLocation;
import net.buildtheearth.buildteamtools.utils.LineRasterization;
import net.buildtheearth.buildteamtools.utils.PolygonTools;
import net.buildtheearth.model.GeographicalCoordinate;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.parser.standard.StringParser;
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
public class KmlCommand {
    private static final String GEOPOINTS_COMMAND = "geopoints";
    private static final String GEOPATH_COMMAND = "geopath";
    private static final String GEORING_COMMAND = "georing";
    private static final String GEOSURFACE_COMMAND = "geosurface";
    private static final String EXTEND_PREFIX = "-extend:";

    private static final NamespacedKey KEY_PLAYER_NAME = namespacedKey("kml_player_name");
    private static final NamespacedKey KEY_BLOCKTYPE = namespacedKey("kml_blocktype");
    private static final NamespacedKey KEY_PREVIOUS_BLOCKTYPE = namespacedKey("kml_previous_blocktype");
    private static final NamespacedKey KEY_BLOCK_CREATION_COMMAND = namespacedKey("kml_block_creation_command");
    private static final NamespacedKey KEY_EXTEND_TO_GROUND = namespacedKey("kml_extend_to_ground");
    private static final NamespacedKey KEY_EXTEND_TO_GROUND_BLOCKTYPE = namespacedKey("kml_extend_to_ground_blocktype");

    private static NamespacedKey namespacedKey(String key) {
        return new NamespacedKey("buildteamtools", key);
    }

    public void register(Module owner) {
        var aliases = BuildTeamTools.getInstance().getMainConfig().commands().labels();
        var commandManager = BuildTeamTools.getInstance().getCommandManager();
        commandManager.registerAliased(owner, "kml", aliases.kml(), this::execute, this::suggestions);
        registerMode(owner, GEOPOINTS_COMMAND, aliases.geopoints());
        registerMode(owner, GEOPATH_COMMAND, aliases.geopath());
        registerMode(owner, GEORING_COMMAND, aliases.georing());
        registerMode(owner, GEOSURFACE_COMMAND, aliases.geosurface());
        registerUndo(owner, "kml");
        registerUndo(owner, GEOPOINTS_COMMAND);
        registerUndo(owner, GEOPATH_COMMAND);
        registerUndo(owner, GEORING_COMMAND);
        registerUndo(owner, GEOSURFACE_COMMAND);
    }

    private void registerUndo(Module owner, String commandName) {
        BuildTeamTools.getInstance().getCommandManager().registerSubcommand(owner, commandName, "undo",
                "Undo the last KML block operation.",
                builder -> builder.optional("arguments", StringParser.greedyStringParser(),
                        RichDescription.of(Component.text("Additional command-block KML content"))),
                context -> {
                    String trailingArguments = context.<String>optional("arguments").orElse("");
                    String[] trailing = trailingArguments.isBlank()
                            ? new String[0]
                            : trailingArguments.trim().split("\\s+");
                    String[] args = new String[trailing.length + 1];
                    args[0] = "undo";
                    System.arraycopy(trailing, 0, args, 1, trailing.length);

                    CommandSender sender = context.sender().getSender();
                    switch (sender) {
                        case BlockCommandSender commandBlockSender -> processKml(commandBlockSender.getBlock(), args);
                        case Player player -> undoCommand(player);
                        default -> execute(sender, commandName, args);
                    }
                });
    }

    private void registerMode(Module owner, String mode, List<String> aliases) {
        BuildTeamTools.getInstance().getCommandManager().registerAliased(owner, mode, aliases,
                (sender, label, arguments) -> execute(sender, mode, arguments), this::suggestions);
    }

    private List<String> suggestions(CommandSender sender, String label, String input) {
        if (label.equalsIgnoreCase("kml")) {
            return BttCommandManager.matchingSuggestions(List.of("undo"), input);
        }
        String partial = input.substring(input.lastIndexOf(' ') + 1).toLowerCase(Locale.ROOT);
        List<String> suggestions = new ArrayList<>();
        if (partial.startsWith(EXTEND_PREFIX)) {
            String blockPrefix = partial.substring(EXTEND_PREFIX.length());
            BLOCK_TYPES.stream().filter(block -> block.startsWith(blockPrefix))
                    .map(block -> EXTEND_PREFIX + block).forEach(suggestions::add);
        } else {
            BLOCK_TYPES.stream().filter(block -> block.startsWith(partial)).forEach(suggestions::add);
            if (EXTEND_PREFIX.startsWith(partial)) suggestions.add(EXTEND_PREFIX);
        }
        suggestions.add("undo");
        return suggestions;
    }

    private static final List<String> BLOCK_TYPES = Arrays.stream(Material.values())
            .filter(Material::isBlock)
            .map(material -> material.name().toLowerCase(Locale.ROOT))
            .toList();

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
     * @param alias  The alias used to invoke this command (geopoints/geopath/georing/kml)
     * @param args   Command arguments (KML content if from CommandBlock, block type if from Player)
     */
    private void execute(@NonNull CommandSender sender, @NonNull String alias, String @NonNull [] args) {
        if (sender instanceof BlockCommandSender cmdbSender) {
            Block senderBlock = cmdbSender.getBlock();

            processKml(senderBlock, args);
            return;
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage(Messages.component(MessageKeys.Generator.Kml.PLAYER_ONLY));
            return;
        }

        if (args.length > 0 && args[0].equals("undo")) {
            undoCommand(p);
            return;
        }

        //check if alias is geopoints or geopath (direct /kml is only allowed for undo)
        if (alias.equals("kml")) {
            sender.sendMessage(Messages.component(MessageKeys.Generator.Kml.USE_GEO_ALIAS));
            return;
        }

        createPasteUI(p, alias, args);
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

        BlockState blockState = senderBlock.getState();
        if (!(blockState instanceof CommandBlock commandBlock)) {
            return false;
        }

        PersistentDataContainer data = commandBlock.getPersistentDataContainer();
        String playerName = data.getOrDefault(KEY_PLAYER_NAME, PersistentDataType.STRING, "");
        String blocktypeString = data.getOrDefault(KEY_BLOCKTYPE, PersistentDataType.STRING, "");
        String previousCommandBlockType = data.getOrDefault(KEY_PREVIOUS_BLOCKTYPE, PersistentDataType.STRING, "");
        String blockCreationCommand = data.getOrDefault(KEY_BLOCK_CREATION_COMMAND, PersistentDataType.STRING, "");
        BlockCreationMode operationMode = commandToCreationMode(blockCreationCommand);

        boolean extendToGround = data.getOrDefault(KEY_EXTEND_TO_GROUND, PersistentDataType.BOOLEAN, false);
        String extendToGroundBlockType = data.getOrDefault(KEY_EXTEND_TO_GROUND_BLOCKTYPE, PersistentDataType.STRING, "");

        if (playerName.isEmpty() || blocktypeString.isEmpty()) {
            //invalid metadata, cancel
            //send error message to all players within 50m of the command block
            Location blockLocation = senderBlock.getLocation();
            double radiusSquared = 50 * 50;
            for (Player nearby : blockLocation.getWorld().getPlayers()) {
                if (nearby.getLocation().distanceSquared(blockLocation) <= radiusSquared) {
                    nearby.sendMessage(Messages.component(MessageKeys.Generator.Kml.COMMANDBLOCK_NO_METADATA));
                }
            }
            return false;
        }

        Player player = Bukkit.getServer().getPlayer(playerName);

        if (player == null) {
            return false;
        }

        recordKml(blockCreationCommand);
        String kml_content = String.join(" ", args);

        Material matchedBlockMaterial = Material.matchMaterial(blocktypeString);
        final Material blockMaterial = (matchedBlockMaterial != null && matchedBlockMaterial.isBlock()) ? matchedBlockMaterial : Material.BRICKS;
        if (matchedBlockMaterial == null || !matchedBlockMaterial.isBlock()) {
            player.sendMessage(Messages.component(MessageKeys.Generator.Kml.INVALID_BLOCK_METADATA));
        }

        Material matchedExtendMaterial = Material.matchMaterial(extendToGroundBlockType);
        final Material extendMaterial = (matchedExtendMaterial != null && matchedExtendMaterial.isBlock()) ? matchedExtendMaterial : Material.BRICKS;
        if (matchedExtendMaterial == null || !matchedExtendMaterial.isBlock()) {
            player.sendMessage(Messages.component(MessageKeys.Generator.Kml.INVALID_BLOCK_METADATA));
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
                StringBuilder details = new StringBuilder();
                details.append("Total positions calculated: ").append(blockPositions.size() + fillPositions.size()).append(". ");
                details.append("Player at: X=").append(player.getLocation().getBlockX());
                details.append(" Y=").append(player.getLocation().getBlockY());
                details.append(" Z=").append(player.getLocation().getBlockZ()).append(". ");

                // Show first calculated block location for debugging
                if (!blockPositions.isEmpty()) {
                    BlockLocation firstBlock = blockPositions.iterator().next();
                    details.append("First block would be at: X=").append(firstBlock.x);
                    details.append(" Y=").append(firstBlock.y);
                    details.append(" Z=").append(firstBlock.z).append(". ");
                }

                if (preventedUnloadedChunkChanges[0]) {
                    details.append("Some blocks are in unloaded chunks. ");
                }
                if (preventedFarChanges[0]) {
                    details.append("Some blocks are >1000 blocks from your location (max distance 1000). ");
                }
                if (!preventedUnloadedChunkChanges[0] && !preventedFarChanges[0]) {
                    details.append("No positions were calculated from the KML. Check your coordinates and command (/btt geopoints, /btt geopath, /btt georing).");
                }
                player.sendMessage(Messages.component(
                        MessageKeys.Generator.Kml.DEBUG_NO_CHANGES,
                        Component.text(details.toString())));
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


                player.sendMessage(Messages.component(
                        MessageKeys.Generator.Kml.TIMING,
                        Component.text(time_afterKMLParse - time_beforeKMLParse),
                        Component.text(time_afterProjection - time_afterKMLParse),
                        Component.text(time_beforeBlockChange - time_afterProjection),
                        Component.text(transaction.size())));

                int blocksChanged = transaction.commit();

                long time_afterBlockChanged = System.currentTimeMillis();
                player.sendMessage(Messages.component(
                        MessageKeys.Generator.Kml.CHANGED_BLOCKS,
                        Component.text(blocksChanged),
                        Component.text(time_afterBlockChanged - time_beforeBlockChange)));


                if (preventedUnloadedChunkChanges[0]) {
                    player.sendMessage(Messages.component(MessageKeys.Generator.Kml.UNLOADED_CHUNKS));
                }
                if (preventedFarChanges[0]) {
                    player.sendMessage(Messages.component(MessageKeys.Generator.Kml.TOO_FAR));
                }

                Material material = Material.matchMaterial(previousCommandBlockType);

                if (material == null) {
                    player.sendMessage(Messages.component(MessageKeys.Generator.Kml.INVALID_BLOCK_METADATA));
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
            result.addAll(LineRasterization.rasterizeLine(tri.vertex1(), tri.vertex2()));
            result.addAll(LineRasterization.rasterizeLine(tri.vertex1(), tri.vertex3()));
            result.addAll(LineRasterization.rasterizeLine(tri.vertex2(), tri.vertex3()));
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
     *   <li>Paste the KML content after "/btt kml "</li>
     *   <li>Set the CommandBlock to "Always Active"</li>
     *   <li>Confirm to execute</li>
     * </ol>
     *
     * @param player The player creating the CommandBlock UI
     * @param alias  The command alias used (determines operation mode)
     * @param args   Command arguments (block type and extend options)
     * @return true if the CommandBlock was created successfully
     */
    public boolean createPasteUI(Player player, String alias, String @NonNull [] args) {
        recordKml(alias);
        //The command either creates a command-block at the player location
        //arguments are an 
        //  optional blocktype 
        //  optional -toGround:blockType
        String blocktype = "BRICKS";
        boolean extendToGround = false;
        String extendToGroundBlockType = "GREEN_WOOL";
        for (String arg : args) {
            if (arg.startsWith(EXTEND_PREFIX)) {
                extendToGround = true;
                extendToGroundBlockType = arg.substring(EXTEND_PREFIX.length()).toUpperCase();

                if (Material.matchMaterial(extendToGroundBlockType) == null) {
                    player.sendMessage(Messages.component(
                            MessageKeys.Generator.Kml.INVALID_EXTEND_BLOCK,
                            Component.text(extendToGroundBlockType)));
                    extendToGroundBlockType = "BRICKS";
                }
            } else {
                blocktype = arg.toUpperCase();

                if (Material.matchMaterial(blocktype) == null) {
                    player.sendMessage(Messages.component(
                            MessageKeys.Generator.Kml.INVALID_BLOCK_TYPE,
                            Component.text(blocktype)));
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

        cmdBlock.setCommand("/btt kml "); //ready to paste kml content
        PersistentDataContainer data = cmdBlock.getPersistentDataContainer();
        data.set(KEY_PLAYER_NAME, PersistentDataType.STRING, player.getName());
        data.set(KEY_BLOCKTYPE, PersistentDataType.STRING, blocktype);
        data.set(KEY_EXTEND_TO_GROUND, PersistentDataType.BOOLEAN, extendToGround);
        data.set(KEY_EXTEND_TO_GROUND_BLOCKTYPE, PersistentDataType.STRING, extendToGroundBlockType);
        data.set(KEY_PREVIOUS_BLOCKTYPE, PersistentDataType.STRING, previousMaterial.toString());
        data.set(KEY_BLOCK_CREATION_COMMAND, PersistentDataType.STRING, alias);

        cmdBlock.update();
        player.sendMessage(Messages.component(MessageKeys.Generator.Kml.COMMANDBLOCK_CREATED));
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
            player.sendMessage(Messages.component(MessageKeys.Generator.Kml.UNDO_NO_HISTORY));
            return false;
        }

        Stack<ChangeTransaction> playerHistory = playerHistories.get(player);

        if (playerHistory == null || playerHistory.empty()) {
            player.sendMessage(Messages.component(MessageKeys.Generator.Kml.UNDO_NOTHING));
            return false;
        }

        FeatureUsage.record(UsageDetail.KML_UNDO);
        ChangeTransaction transaction = playerHistory.pop();
        transaction.undo();
        player.sendMessage(Messages.component(
                MessageKeys.Generator.Kml.UNDO_SUCCESS,
                Component.text(transaction.size())));
        return true;
    }

    /**
     * Converts geographic coordinates (latitude/longitude) to a Minecraft Location.
     * <p>
     * This method:
     * <ol>
     *   <li>Converts lat/lon to Minecraft X/Z using </li>
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
        Location mcLocation = NavUtils.getLocationFromCoordinates(new GeographicalCoordinate(coordinates[0], coordinates[1]));
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

    private static void recordKml(@NonNull String command) {
        UsageDetail detail = switch (command) {
            case GEOPOINTS_COMMAND -> UsageDetail.KML_GEOPOINTS;
            case GEOPATH_COMMAND -> UsageDetail.KML_GEOPATH;
            case GEORING_COMMAND -> UsageDetail.KML_GEORING;
            case GEOSURFACE_COMMAND -> UsageDetail.KML_GEOSURFACE;
            default -> null;
        };
        if (detail == null)
            FeatureUsage.record(UsageFeature.KML);
        else
            FeatureUsage.record(detail);
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
            case GEOPOINTS_COMMAND -> BlockCreationMode.POINTS;
            case GEOPATH_COMMAND -> BlockCreationMode.PATH;
            case GEORING_COMMAND -> BlockCreationMode.CLOSED_PATH;
            case GEOSURFACE_COMMAND ->
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
