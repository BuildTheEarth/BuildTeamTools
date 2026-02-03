package net.buildtheearth.buildteamtools.modules.common.metrics;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class MetricsManager {

    private static final int BSTATS_PLUGIN_ID = 29076;

    public static void init(JavaPlugin plugin) {
        new Metrics(plugin, BSTATS_PLUGIN_ID);
    }
}
