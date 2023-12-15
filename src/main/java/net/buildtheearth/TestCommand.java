package net.buildtheearth;

import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.network.model.Country;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String result = "";
        for(Continent continent : Continent.values()) {
            for(Country country : continent.getCountries()) {
                if(country.isConnected()) {
                    result += country.getName();
                    result += country.getServerName();
                    result += country.getContinent().getLabel();
                }
            }
        }
        Bukkit.getLogger().info(result == null ? "null" : result);
        return true;
    }
}
