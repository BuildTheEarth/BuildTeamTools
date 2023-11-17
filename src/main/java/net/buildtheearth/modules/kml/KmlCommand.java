package net.buildtheearth.modules.kml;

import net.buildtheearth.Main;
import net.buildtheearth.modules.utils.GeometricUtils;
import net.buildtheearth.modules.utils.LineRasterization;
import net.buildtheearth.modules.utils.geo.LatLng;

import java.util.List;
import java.util.ArrayList;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.BlockCommandSender;

import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.LineString;

import org.bukkit.World;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;

public class KmlCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if (sender instanceof BlockCommandSender){
            Bukkit.getServer().broadcastMessage(
                    String.format("----SERVER broadcast: received /kml command from commandblock with %d arguments----", args.length)); 
            //TODO find player who created the block?

            //parse kml
            KmlParser parser = new KmlParser();
            List<LineString> geoLines = parser.extractLinestrings(String.join(" ", args));

            World world = ((BlockCommandSender)sender).getBlock().getWorld();
            List<List<Location>> mcLines = convertToMC(geoLines, world);

            for (List<Location> polyline : mcLines)
            {
                //rasterize line and create intermediate blocks
                //note: iteration starts at second block, so we always have a previous block to draw the line

                for (int i = 1; i < polyline.size(); ++i)
                {
                    LineRasterization.fillLineWithBlocks(polyline.get(i-1), polyline.get(i), world, Material.GOLD_BLOCK);
                    //set only single block
                    //world.getBlockAt(loc).setType(Material.GOLD_BLOCK);
                }
                
            }
            

            //TODO destroy the block
            return true;
        }
        if(!(sender instanceof Player)){
            sender.sendMessage("§cOnly players can execute this command.");
            return true;
        }

        Player p = (Player) sender;

        p.sendMessage(String.format("----received /kml command with %d arguments----", args.length));

        //The command either creates a command-block at the player location
        //command handling will handled server-side with CommandBlock as CommandSender
        Block targetedBlock = p.getTargetBlock(null, 10);
        if (targetedBlock == null || targetedBlock.getType() != Material.COMMAND)
        {
            //spawn a command block at the player location
            Location blockLocation = p.getLocation().add(0, 0, 0);
            
            World world = blockLocation.getWorld();
            Block block = world.getBlockAt(blockLocation);

            block.setType(Material.COMMAND);    

            //for now, user has to manually set the command to "auto" to get it immediately triggered on confirm
            CommandBlock cmdBlock = (CommandBlock) block.getState();
            cmdBlock.setCommand("/kml "); //ready to paste kml content
            //TODO maybe with blocktype selection as argument
            cmdBlock.update();
            p.sendMessage("§cCommand block created. Please open the GUI, paste the KML content, set it to 'always on' and confirm");
            
        } 
      
        //sendHelp(p);
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
                mcLocation.add(0, coordinate.getAltitude(), 0);
                //System.out.println(mcLocation);

                mcLine.add(mcLocation);
            }
            mcLines.add(mcLine);
        }
        return mcLines;
    }
}
