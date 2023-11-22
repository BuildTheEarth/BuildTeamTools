package net.buildtheearth.modules.kml;

import net.buildtheearth.Main;
import net.buildtheearth.modules.utils.BlockLocation;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.GeometricUtils;
import net.buildtheearth.modules.utils.LineRasterization;
import net.buildtheearth.modules.utils.geo.LatLng;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.BlockCommandSender;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.LineString;

import org.bukkit.World;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;

import org.bukkit.metadata.FixedMetadataValue;



public class KmlCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if (sender instanceof BlockCommandSender){
            Block senderBlock = ((BlockCommandSender)sender).getBlock();

            return processKml(senderBlock, args);
        }

        if(!(sender instanceof Player)){
            sender.sendMessage("§cOnly players can execute this command.");
            return true;
        }


        Player p = (Player) sender;

        //TESTING location/elevation code
        Block targetedBlock = p.getTargetBlock(null, 10);
        Location targetedLocation = targetedBlock.getLocation();
        int highY = p.getWorld().getHighestBlockYAt(targetedLocation);
        p.sendMessage(String.format("elevation test: targeted block (%s) location Y %d (%f), highestY %d",
            targetedBlock.getType().toString(), targetedLocation.getBlockY(), targetedLocation.getY(),highY));
        
        if (args.length > 0 && args[0].equals("undo")){
            return undoCommand(p);
        }            

        return createPasteUI(p, cmd, args);
    }


    public boolean processKml(Block senderBlock, String[] args){

        //read metadata to get player name/ID
        BlockState blockState = senderBlock.getState();
        String playerName = blockState.getMetadata("kmlPlayerName").get(0).asString(); 
        String blocktypeString = blockState.getMetadata("kmlBlocktype").get(0).asString();
        String previousCommandBlockType = blockState.getMetadata("kmlPreviousBlocktype").get(0).asString();
                
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

        Material blockMaterial = Material.getMaterial(blocktypeString);
        if (blockMaterial == null){
            player.sendMessage("§creceived /kml command with invalid blocktype string. Using bricks as fallback.");
            blockMaterial = Material.BRICK;
        }

        //parse kml
        KmlParser parser = new KmlParser();
        List<LineString> geoLines = parser.extractLinestrings(String.join(" ", args));

        World world = senderBlock.getWorld();
        List<List<Location>> mcLines = convertToMC(geoLines, world);
        Location tpLoc = null;



        //set up a transaction (collection of block changes)
        ChangeTransaction transaction = new ChangeTransaction(player);
        
        //collect all blocklocations in a set
        //if just iterate and create blocks one by one, we create multiple blocks at the same XZ coordinates,
        // this also stacks them vertically because we check terrain altitude.
        Set<BlockLocation> blockPositions = new HashSet<>();

        for (List<Location> polyline : mcLines)
        {
            //rasterize line and create intermediate blocks
            //note: iteration starts at second block, so we always have a previous block to draw the line

            for (int i = 1; i < polyline.size(); ++i)
            {
                blockPositions.addAll(
                    LineRasterization.rasterizeLine(polyline.get(i-1), polyline.get(i)));

                if (tpLoc == null)
                {
                    tpLoc = polyline.get(0);
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
        int blocksChanged = transaction.commit();
        
        player.sendMessage(String.format("imported KML data. Changed %d blocks.", blocksChanged));
        

        if (preventedUnloadedChunkChanges){
            player.sendMessage("§cSome block changes target unloaded chunks and were not applied.");
        }
        if (preventedFarChanges){
            player.sendMessage("§cSome block changes target blocks too far away from the player and were not applied.");
        }

        //Delete command block, restore previous type
        senderBlock.setType(Material.getMaterial(previousCommandBlockType));
        return true;
    }

    public boolean createPasteUI(Player player, Command cmd, String[] args){
        //The command either creates a command-block at the player location
        //TODO TabCompleter

        String blocktype = "bricks";
        if (args.length > 0)
        {
            blocktype = args[0];
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
        cmdBlock.setMetadata("kmlPlayerName", new FixedMetadataValue(Main.instance, player.getName()));
        //cmdBlock.setMetadata("kmlPlayerID", new FixedMetadataValue(Main.instance, p.getUniqueId()));
        cmdBlock.setMetadata("kmlBlocktype", new FixedMetadataValue(Main.instance, blocktype));
        block.setMetadata("kmlPreviousBlocktype", new FixedMetadataValue(Main.instance, previousMaterial.toString()));
        
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

    private List<List<org.bukkit.Location> > convertToMC(List<LineString> geoLines, World world){
        List<List<org.bukkit.Location> > mcLines = new ArrayList<>();

        for (LineString line : geoLines){
            List<org.bukkit.Location> mcLine =new ArrayList<>();

            for (Coordinate coordinate : line.getCoordinates()) {
                //convert lat/lon to minecraft x z
                LatLng coordinates = new LatLng(coordinate.getLatitude(), coordinate.getLongitude());

                // convert with projection and extract terrain altitude
                Location mcLocation = GeometricUtils.getLocationFromCoordinates(coordinates);
                //add altitude from kml (altitude from Google Earth is always relative to ground)
                //note: the "-2" is only neccesary because 
                //  getLocationFromCoordinates returns terrain altitude + 2
                //      (one from Bukkits getHighestBlockY and one from our geoutils)
                mcLocation.add(0, coordinate.getAltitude() - 2, 0);

                mcLine.add(mcLocation);
            }
            mcLines.add(mcLine);
        }
        return mcLines;
    }

    private HashMap<Player, Stack<ChangeTransaction>> playerHistories;
}
