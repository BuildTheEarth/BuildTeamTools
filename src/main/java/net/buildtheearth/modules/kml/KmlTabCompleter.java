package net.buildtheearth.modules.kml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class KmlTabCompleter implements TabCompleter{
    public KmlTabCompleter(){
        //compile list of block material types for completion
        blocktypes = new ArrayList<String>();
        for(Material mat : Material.values()) {
            if (mat.isBlock())
                blocktypes.add(mat.toString().toLowerCase());
        }        
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        //the player can use /geopoints and /geopath with a single argument: "undo" or Blocktype
        //
        List<String> completions = new ArrayList<>();

        if (sender instanceof Player) {
            //alias kml only has undo as return option
            if (alias.equalsIgnoreCase("kml"))
                completions.add("undo");
            else if (args.length == 1) {
                //player has started to type the first argument

                //add all blocktypes that match the characters of the argument
                for (String blocktype : blocktypes){
                    if (blocktype.startsWith(args[0].toLowerCase())){
                        completions.add(blocktype);
                    }
                }

                Collections.sort(completions);

                completions.add("undo");

            }
        }

        return completions;
    }

    private List<String> blocktypes;
}
