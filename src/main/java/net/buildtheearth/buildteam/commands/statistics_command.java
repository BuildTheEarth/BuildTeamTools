package net.buildtheearth.buildteam.commands;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.stats.menu.StatsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class statistics_command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        new StatsMenu(player);
        return true;
    }
}
