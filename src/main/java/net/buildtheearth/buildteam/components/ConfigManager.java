package net.buildtheearth.buildteam.components;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import net.buildtheearth.utils.Config;

public class ConfigManager {

	public static void readData() {
        FileConfiguration cfg = Config.getFileConfiguration("config");
	}
	
	public static void setStandard() {
		FileConfiguration cfg = Config.getFileConfiguration("config");
	    cfg.options().copyDefaults(true);
	    
	    try {
	        cfg.save(Config.getFile("config"));
	    } catch (IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "An error occurred, trying to save the config file!", e);
	    }
	}
}
