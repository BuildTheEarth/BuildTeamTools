package net.buildtheearth;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String result = Main.getBuildTeamTools().getProxyManager().getActiveServers().toString();
        Bukkit.getLogger().info(result == null ? "null" : result);
        return true;
    }
}
