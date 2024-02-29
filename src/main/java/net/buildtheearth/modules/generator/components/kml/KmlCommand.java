package net.buildtheearth.modules.generator.components.kml;

import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.utils.BlockLocation;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.GeometricUtils;
import net.buildtheearth.modules.utils.LineRasterization;
import net.buildtheearth.modules.utils.PolygonTools;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.BlockCommandSender;

import org.bukkit.entity.Player;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;

import org.bukkit.World;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;

import org.bukkit.metadata.FixedMetadataValue;


    /** Class to handle kml command and its aliases
     *  
     * Since kml content easily exceeds the normal chat/character limit, 
     * we use the following workflow: 
     * 
     * if sent from a player, generates a CommandBlock and adds some metadata to it
     * the player then pastes the kml contents and confirms the command
     * 
     * the CommandBlock then sends a kml command with the contents, which is parsed by the server
     * 
     * note: this command differentiates between aliases to determine wether to 
     * generate intermediate points between the geo-coordinates from the kml linestrings.
     * Players should only use /geopoints or /geopath aliases, 
     * direct use of the /kml command is restricted to CommandBlockSender
     * 
     */
public class KmlCommand implements CommandExecutor {

    
    /** Command handling
     *  
     */
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (sender instanceof BlockCommandSender){
            Block senderBlock = ((BlockCommandSender)sender).getBlock();

            return processKml(senderBlock, args);
        }

        if(!(sender instanceof Player)){
            sender.sendMessage("§cOnly players can execute this command.");
            return false;
        }


        Player p = (Player) sender;
        if (args.length > 0 && args[0].equals("undo")){
            return undoCommand(p);
        }  

        // //TESTING location/elevation code
        // Block targetedBlock = p.getTargetBlock(null, 10);
        // Location targetedLocation = targetedBlock.getLocation();
        // int highY = p.getWorld().getHighestBlockYAt(targetedLocation);
        // p.sendMessage(String.format("elevation test: targeted block (%s) location Y %d (%f), highestY %d",
        //     targetedBlock.getType().toString(), targetedLocation.getBlockY(), targetedLocation.getY(),highY));
        
          
        //check if alias is geopoints or geopath (direct /kml is only allowed for undo)
        if (alias.equals("kml")){
            sender.sendMessage("§cPlease use /geopoints or /geopath to execute this command.");
            return false;
        }

        return createPasteUI(p, cmd, alias, args);
    }

    void addSingleLocation(BlockLocation loc, boolean extendToGround, World world, Set<BlockLocation> container, Set<BlockLocation> fillPositions)
    {
        container.add(loc);
        if (extendToGround)
            fillVerticalToTerrain(loc, world, fillPositions);
    }

    void addLocations(List<BlockLocation> locs, boolean extendToGround, World world, Set<BlockLocation> container, Set<BlockLocation> fillPositions)
    {
        for (BlockLocation loc : locs)
            addSingleLocation(loc, extendToGround, world, container, fillPositions);
    }

    void fillVerticalToTerrain(BlockLocation locationOverGround, World world, Set<BlockLocation> container)
    {
        int terrainElevation = world.getHighestBlockYAt(locationOverGround.x, locationOverGround.z);
        for (int y = terrainElevation; y < locationOverGround.y; y++){
            container.add(new BlockLocation(locationOverGround.x, y, locationOverGround.z));
        }
    }
    /** 
     * @param senderBlock
     * @param args
     * @return boolean
     */
    public boolean processKml(Block senderBlock, String[] args){
        long time_start = System.currentTimeMillis();


        //read metadata to get player name/ID
        BlockState blockState = senderBlock.getState();
        String playerName = blockState.getMetadata("kmlPlayerName").get(0).asString(); 
        String blocktypeString = blockState.getMetadata("kmlBlocktype").get(0).asString();
        String previousCommandBlockType = blockState.getMetadata("kmlPreviousBlocktype").get(0).asString();
        String blockCreationCommand = blockState.getMetadata("kmlBlockCreationCommand").get(0).asString();
        BlockCreationMode operationMode = commandToCreationMode(blockCreationCommand);

        boolean extendToGround = blockState.getMetadata("kmlExtendToGround").get(0).asBoolean();
        String extendToGroundBlockType = blockState.getMetadata("kmlExtendToGroundBlocktype").get(0).asString();
        

        if (playerName == "" || blocktypeString == ""){
            //invalid metadata, cancel
            //send error message to all players within 50m of the command block
            ChatHelper.sendMessageToPlayersNearLocation(
                senderBlock.getLocation(),
                "§cReceived /kml command from CommandBlock without sufficient metadata.\nThis command can only be executed from a CommandBlock created with the /kml command!",
                50); 
            return false;
        }

        Player player = Bukkit.getServer().getPlayer(playerName);
        
        if (player == null){
            return false;
        }

        String kml_content = String.join(" ", args);
        // player.sendMessage(String.format("§creceived /kml command with blocktypestring %s and and kml length %d: %s",
        //  blocktypeString, kml_content.length(), kml_content));

        Material blockMaterial = Material.matchMaterial(blocktypeString);
        if (blockMaterial == null){
            player.sendMessage("§cServer received /kml command with invalid blocktype string metadata. Using bricks as fallback.");
            blockMaterial = Material.BRICK;
        }
        Material extendMaterial = Material.matchMaterial(extendToGroundBlockType);
        if (extendMaterial == null){
            player.sendMessage("§cServer received /kml command with invalid blocktype string metadata. Using bricks as fallback.");
            extendMaterial = Material.BRICK;
        }


        //parse kml
        long time_beforeKMLParse = System.currentTimeMillis();



        KmlParser parser = new KmlParser(player); // we pass the player here to be able to report parsing errors
        List<List<Coordinate>> geoCoords = parser.extractCoordinates(kml_content);
        long time_afterKMLParse = System.currentTimeMillis();


        World world = senderBlock.getWorld();

        List<List<Location>> mcLocations = convertToMC(geoCoords, world);
        long time_afterProjection = System.currentTimeMillis();


        //set up a transaction (collection of block changes)
        ChangeTransaction transaction = new ChangeTransaction(player);
        
        //collect all blocklocations in a set
        //if just iterate and create blocks one by one, we create multiple blocks at the same XZ coordinates,
        // this also stacks them vertically because we check terrain altitude.
        Set<BlockLocation> blockPositions = new HashSet<>();
        Set<BlockLocation> fillPositions = new HashSet<>();



        for (List<Location> polyline : mcLocations)
        {
            if (operationMode == BlockCreationMode.FILLED)
                blockPositions = triangulateAndFill(polyline);
            else{
                //rasterize line and create intermediate blocks
                //note: iteration starts at second block, so we always have a previous block to draw the line
                //for single point mode, we explicitly add the first block
                if (operationMode == BlockCreationMode.POINTS)
                {
                    BlockLocation loc = new BlockLocation(polyline.get(0));
                    addSingleLocation(loc, extendToGround, world, blockPositions, fillPositions);
                }

                for (int i = 1; i < polyline.size(); ++i)
                {
                    if (operationMode == BlockCreationMode.POINTS)
                    {
                        BlockLocation loc = new BlockLocation(polyline.get(i));
                        addSingleLocation(loc, extendToGround, world, blockPositions, fillPositions);
                    }
                    else //interpolate
                    {
                        addLocations(LineRasterization.rasterizeLine(polyline.get(i-1), polyline.get(i)),
                            extendToGround, world, blockPositions, fillPositions);
                    }
                }

                //for closed-path-mode, add extra line between start and end
                if (operationMode == BlockCreationMode.CLOSED_PATH)
                {
                    addLocations(LineRasterization.rasterizeLine(polyline.get(0), polyline.get(polyline.size()-1))
                        , extendToGround, world, blockPositions, fillPositions);
                }
            }
            
        }

        //now create the blocks
        // restrictions: only if block is loaded, and only within 1000 blocks of the current player location
        boolean preventedUnloadedChunkChanges = false;
        boolean preventedFarChanges = false;

        float maxDistanceToPlayer = 1000; //TODO server config

        for (BlockLocation pt : blockPositions)
        {
            Location loc = pt.getLocation(world);
            if (!loc.getChunk().isLoaded())
                preventedUnloadedChunkChanges = true;
            else if (loc.distance(player.getLocation()) > maxDistanceToPlayer)
                preventedFarChanges = true;
            else
            {
                transaction.addBlockChange(pt, world, blockMaterial);
            }
        }

        for (BlockLocation pt : fillPositions)
        {
            Location loc = pt.getLocation(world);
            if (!loc.getChunk().isLoaded())
                preventedUnloadedChunkChanges = true;
            else if (loc.distance(player.getLocation()) > maxDistanceToPlayer)
                preventedFarChanges = true;
            else
            {
                transaction.addBlockChange(pt, world, extendMaterial);
            }
        }


        if (transaction.size() == 0){
            player.sendMessage("§ckml command did not contain any allowed block changes.\nThis command can only change blocks near your current location, and cannot load new chunks.");
            return false;
        }

        //create commandHistory if not exits
        if (this.playerHistories == null)
            this.playerHistories = new HashMap<>();

        if (! playerHistories.containsKey(player))
            playerHistories.put(player, new Stack<>());

        Stack<ChangeTransaction> playerHistory = playerHistories.get(player);

        playerHistory.push(transaction);


        long time_beforeBlockChange = System.currentTimeMillis();


        player.sendMessage(String.format("KML parsing: %d ms. BTE Projection: %d ms, Transaction preparation: %d ms. Changing %d blocks, please stand by."
            , (time_afterKMLParse-time_beforeKMLParse)
            , (time_afterProjection - time_afterKMLParse )
            , (time_beforeBlockChange - time_afterProjection), transaction.size()));
        
        int blocksChanged = transaction.commit();

        long time_afterBlockChanged = System.currentTimeMillis();
        player.sendMessage(String.format("KML command changed %d blocks (%d ms).", blocksChanged, (time_afterBlockChanged - time_beforeBlockChange)));
        

        if (preventedUnloadedChunkChanges){
            player.sendMessage("§cSome block changes target unloaded chunks and were not applied.");
        }
        if (preventedFarChanges){
            player.sendMessage("§cSome block changes target blocks too far away from the player and were not applied.");
        }

        //Delete command block, restore previous type
        senderBlock.setType(Material.matchMaterial(previousCommandBlockType));
        return true;
    }

    private Set<BlockLocation> triangulateAndFill(List<Location> polyline) {
        Set<BlockLocation>result = new HashSet<>();
        //triangulate the polygon
        List<PolygonTools.Triangle> triangles = PolygonTools.triangulatePolygon(polyline);
        //fill with bresenham. for now, just all border-lines
        for (PolygonTools.Triangle tri : triangles){
            result.addAll(PolygonTools.rasterizeTriangle(tri));
            
            //dEBUG: ALL triangle borders
            result.addAll(LineRasterization.rasterizeLine(tri.getVertex1(), tri.getVertex2()));
            result.addAll(LineRasterization.rasterizeLine(tri.getVertex1(), tri.getVertex3()));
            result.addAll(LineRasterization.rasterizeLine(tri.getVertex2(), tri.getVertex3()));
        }
        return result;
    }



    public boolean createPasteUI(Player player, Command cmd, String alias, String[] args){
        //The command either creates a command-block at the player location
        //arguments are an 
        //  optional blocktype 
        //  optional -toGround:blockType
        String blocktype = "BRICK";
        boolean extendToGround = false;
        String extendToGroundBlockType = "GREEN_WOOL";
        String prefix_extendParam = "-extend:";
        for (String arg : args)
        {
            if (arg.startsWith(prefix_extendParam)){
                extendToGround = true;
                extendToGroundBlockType = arg.substring(prefix_extendParam.length()).toUpperCase();
                if (Material.matchMaterial(extendToGroundBlockType) == null)
                {
                    player.sendMessage(String.format("§cInvalid block type for extend parameter '%s'. Using bricks as fallback.", extendToGroundBlockType));
                    extendToGroundBlockType = "BRICK";
                }
            }else{
                blocktype = arg.toUpperCase();
                if (Material.matchMaterial(blocktype) == null)
                {
                    player.sendMessage(String.format("§cInvalid block type '%s'. Using bricks as fallback.", blocktype));
                    blocktype = "BRICK";
                }
            }

                
        }


        //spawn a command block in front of the player
        Location commandBlockLocation = player.getLocation().add(player.getLocation().getDirection().multiply(2));
        commandBlockLocation = commandBlockLocation.add(0, 2, 0);

        World world = commandBlockLocation.getWorld();
        Block block = world.getBlockAt(commandBlockLocation);

        Material previousMaterial = block.getType(); //remember old blocktype to replace after command processing
        block.setType(Material.COMMAND);    
        
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

    public boolean undoCommand(Player player){
        if (playerHistories==null){
            player.sendMessage("kml undo failed - no command history available.");
            return false;
        }

        Stack<ChangeTransaction> playerHistory = playerHistories.get(player);
        
        if (playerHistory == null || playerHistory.empty()){
            player.sendMessage("kml undo failed - there is no previously executed kml command.");
            return false;
        }

        ChangeTransaction transaction = playerHistory.pop();
        transaction.undo();
        player.sendMessage(String.format("undo successful. Restored %d blocks.", transaction.size()));
        return true;
    }

    public static void sendHelp(CommandSender sender){
        //ChatUtil.sendMessageBox(sender, "GML Command", () -> {
        //    sender.sendMessage("§eHouse Generator:§7 /gml help");
        //});
    }

    private Location getLocationFromCoordinates(double[] coordinates, double altitudeFromKML)
    {
        Location mcLocation = GeometricUtils.getLocationFromCoordinates(coordinates);
        //add altitude from kml (altitude from Google Earth is always relative to ground)
        //note: the "-2" is only neccesary because 
        //  getLocationFromCoordinates returns terrain altitude + 2
        //      (one from Bukkits getHighestBlockY and one from our geoutils)
        mcLocation.add(0, altitudeFromKML - 2, 0);
        return mcLocation;
    }

    private List<List<org.bukkit.Location> > convertToMC(List<List<Coordinate>> geocoords_lists, World world){
    
        List<List<org.bukkit.Location> > mcLines = new ArrayList<>();

        //This lat/long to xyz conversion takes most of the runtime for this command
        // since the conversions do not influence each other, we can parallelize them.
        for (List<Coordinate> geocoords : geocoords_lists){
            // ExecutorService to manage the threads
            ExecutorService executorService = Executors.newFixedThreadPool(geocoords.size());

            // List to store CompletableFuture results
            List<CompletableFuture<Location>> completableFutureList = new ArrayList<>();

            // Create a CompletableFuture for each set of coordinates
            for (Coordinate geocoord : geocoords) {
                double[] coordinates = new double[]{geocoord.getLatitude(), geocoord.getLongitude()};

                CompletableFuture<Location> completableFuture = CompletableFuture.supplyAsync(() ->
                        getLocationFromCoordinates(coordinates, geocoord.getAltitude()), executorService);
                completableFutureList.add(completableFuture);
            }

            // Combine all CompletableFutures into a single CompletableFuture representing all of them
            CompletableFuture<Void> allOf = CompletableFuture.allOf(
                    completableFutureList.toArray(new CompletableFuture[0])
            );

            // Wait for all CompletableFutures to complete
            allOf.join();

            // Collect the results from the CompletableFutures
            List<org.bukkit.Location> mcLine = completableFutureList.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            mcLines.add(mcLine);

            // Shutdown the ExecutorService
            executorService.shutdown();
        }
        return mcLines;
    }

    private BlockCreationMode commandToCreationMode(String command){
        if (command.equals("geopoints"))
            return BlockCreationMode.POINTS;
        else if (command.equals("geopath"))
            return BlockCreationMode.PATH;
        else if (command.equals("georing"))
            return BlockCreationMode.CLOSED_PATH;
        else if (command.equals("geosurface"))
        {
            throw new UnsupportedOperationException("Operation mode 'filled surface' is not yet implemented");
            //return BlockCreationMode.FILLED;
        }
        else
            throw new IllegalArgumentException(command);
            
    }

    private HashMap<Player, Stack<ChangeTransaction>> playerHistories;
}
