package net.buildtheearth.buildteam.commands;

import net.buildtheearth.buildteam.components.universal_experience.MainMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMD_Navigator implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED +"You must be a player to run this command");
            return true;
        }

        //Opens the navigator
        new MainMenu((Player) sender);

        return true;
    }
}
