package net.buildtheearth.buildteam.commands;

import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.geo.OpenStreetMapAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMD_Warp implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatHelper.highlight("This command can only be used by a player!"));
            return true;
        }

        Player player = (Player) sender;
        if(args[0].equalsIgnoreCase("create")) {
            // Check if the required arguments are provided & send usage otherwise
            if(args.length != 4) return false;

            String key = args[1];
            double lat;
            double lon;
            try {
                lat = Double.parseDouble(args[2]);
                lon = Double.parseDouble(args[3]);
            } catch (NullPointerException | NumberFormatException e) {
                player.sendMessage(ChatHelper.highlight("Incorrect arguments!"));
                return false;
            }


            // Check if the player has the required permissions
            if(!player.hasPermission("btt.warp.create")) {
                player.sendMessage(ChatHelper.highlight("You don't have the required %s to %s warps.", "permissions", "create"));
            }

            String countryCode = OpenStreetMapAPI.getCountryFromLocation(new double[]{lat, lon});

            //TODO CHECK IF THIS TEAM OWNS THE COUNTRY, IF THEY DO CREATE THE WARP

        }

        return false;
    }
}
