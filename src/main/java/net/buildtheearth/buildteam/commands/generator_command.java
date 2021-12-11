package net.buildtheearth.buildteam.commands;

import net.buildtheearth.buildteam.components.generator.Inventories;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class generator_command implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String cmdlabel, String[] args) {
        Player p = (Player) sender;

        Inventories.openGeneratorInventory(p);

        return true;
    }
}
