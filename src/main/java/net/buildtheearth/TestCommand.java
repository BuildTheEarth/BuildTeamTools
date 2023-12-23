package net.buildtheearth;

import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.network.model.Region;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String result = "";
        for(Continent continent : Continent.values()) {
            for(Region region : continent.getRegions()) {
                if(region.isConnected()) {
                    result += region.getName();
                    result += region.getBuildTeam().getServerName();
                    result += region.getContinent().getLabel();
                }
            }
        }
        Bukkit.getLogger().info(result == null ? "null" : result);
        return true;
    }
}
