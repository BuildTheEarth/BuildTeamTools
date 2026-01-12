package net.buildtheearth.buildteamtools.modules.generator.components.kml;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            else if (args.length > 0){
                //player has started to type an argument
                //allowed arguments are blocktype and/or -extend:blocktype
                String currentArg = args[args.length-1];
                if (currentArg.startsWith("-")){
                    if (currentArg.startsWith("-extend:")){
                        addMatchingBlocktypeCompletions(currentArg, completions, "-extend:");
                    }else{
                        completions.add("-extend:");
                    }
                }
                else {
                    //add all blocktypes that match the characters of the argument
                    addMatchingBlocktypeCompletions(currentArg, completions, "");
                }

                Collections.sort(completions);

                completions.add("undo");

            }
        }

        return completions;
    }

    private void addMatchingBlocktypeCompletions(String currentArg, List<String> completions, String prefix)
    {
        //add all blocktypes that match the characters of the argument
        String argWithoutPrefix = currentArg.substring(prefix.length());
        for (String blocktype : blocktypes){
            if (blocktype.startsWith(argWithoutPrefix.toLowerCase())){
                completions.add(prefix + blocktype);
            }
        }
    }
    private List<String> blocktypes;
}
