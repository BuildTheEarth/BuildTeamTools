package net.buildtheearth.buildteamtools.modules.common.metrics;

import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.ModuleHandler;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.modules.navigation.components.bluemap.BluemapComponent;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.MultiLineChart;
import org.bstats.charts.SimpleBarChart;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * bStats metrics for BuildTeamTools.
 * <p>
 * {@code feature_uses} is a bar chart that lists every tracked feature. A zero means it was not used
 * in that submission. bStats drops zero values from line charts, so {@code feature_use_history} repeats
 * the same counts as a line chart and only draws features that were actually used. Both charts share
 * one snapshot so a submission does not reset the counters twice.
 * <p>
 * BlueMap has no player action in this plugin. It is reported as 1 while the integration is enabled.
 * <p>
 * {@code feature_details} is a drilldown pie. Clicking navigator, warps, address, or kml opens the
 * part that was used. It shares the snapshot with the summary charts.
 */
public final class MetricsManager {

    private static final int BSTATS_PLUGIN_ID = 29076;
    /** Long enough for every chart in one submission to share a snapshot, shorter than the 30 minute submit interval. */
    private static final long SNAPSHOT_REUSE_NANOS = TimeUnit.MINUTES.toNanos(2);

    private static Metrics metrics;
    private static PublishedSnapshot published;

    private MetricsManager() {
    }

    public static void init(JavaPlugin plugin) {
        if (metrics != null)
            metrics.shutdown();

        Metrics created = new Metrics(plugin, BSTATS_PLUGIN_ID);
        created.addCustomChart(new SimpleBarChart("feature_uses", MetricsManager::featureUseCounts));
        created.addCustomChart(new MultiLineChart("feature_use_history", MetricsManager::featureUseCounts));
        created.addCustomChart(new DrilldownPie("feature_details", MetricsManager::featureDetails));
        created.addCustomChart(new AdvancedPie("enabled_modules", MetricsManager::enabledModules));
        metrics = created;
    }

    static Map<String, Integer> featureUseCounts() {
        return featureUseCounts(System.nanoTime());
    }

    static Map<String, Integer> featureUseCounts(long nowNanos) {
        return new LinkedHashMap<>(publish(nowNanos).counts());
    }

    static Map<String, Map<String, Integer>> featureDetails() {
        return featureDetails(System.nanoTime());
    }

    static Map<String, Map<String, Integer>> featureDetails(long nowNanos) {
        Map<String, Map<String, Integer>> copy = new LinkedHashMap<>();
        publish(nowNanos).details().forEach((feature, parts) -> copy.put(feature, new LinkedHashMap<>(parts)));
        return copy;
    }

    private static PublishedSnapshot publish(long nowNanos) {
        synchronized (MetricsManager.class) {
            if (published != null && nowNanos >= published.atNanos() && nowNanos - published.atNanos() < SNAPSHOT_REUSE_NANOS)
                return published;

            Map<String, Integer> counts = FeatureUsage.drain();
            counts.put("bluemap", blueMapInUse());
            published = new PublishedSnapshot(counts, FeatureUsage.drainDetails(), nowNanos);
            return published;
        }
    }

    static Map<String, Integer> enabledModules() {
        Map<String, Integer> enabled = new LinkedHashMap<>();
        for (Module module : ModuleHandler.getInstance().getModules())
            if (module.isEnabled())
                enabled.put(module.getModuleName(), 1);
        return enabled;
    }

    private static int blueMapInUse() {
        NavigationModule navigation = NavigationModule.getInstance();
        if (navigation == null)
            return 0;

        BluemapComponent bluemap = navigation.getBluemapComponent();
        return bluemap != null && bluemap.isEnabled() ? 1 : 0;
    }

    static void resetForTests() {
        synchronized (MetricsManager.class) {
            published = null;
        }
        FeatureUsage.drain();
        FeatureUsage.drainDetails();
    }

    private record PublishedSnapshot(
            Map<String, Integer> counts,
            Map<String, Map<String, Integer>> details,
            long atNanos
    ) {
    }
}
