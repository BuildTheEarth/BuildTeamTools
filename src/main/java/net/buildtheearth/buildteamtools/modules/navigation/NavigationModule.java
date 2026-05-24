package net.buildtheearth.buildteamtools.modules.navigation;

import com.alpsbte.alpslib.geo.rgc.RgcHandler;
import com.alpsbte.alpslib.utils.ChatHelper;
import lombok.Getter;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.navigation.components.bluemap.BluemapComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.navigator.NavigatorComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands.BuildteamCommand;
import net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands.ExploreCommand;
import net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands.NavigatorCommand;
import net.buildtheearth.buildteamtools.modules.navigation.components.navigator.listeners.NavigatorJoinListener;
import net.buildtheearth.buildteamtools.modules.navigation.components.navigator.listeners.NavigatorOpenListener;
import net.buildtheearth.buildteamtools.modules.navigation.components.tpll.TpllComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.tpll.listeners.TpllJoinListener;
import net.buildtheearth.buildteamtools.modules.navigation.components.tpll.listeners.TpllListener;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.commands.WarpCommand;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.commands.WarpsBtCommand;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.listeners.WarpJoinListener;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.utils.WikiLinks;
import net.buildtheearth.buildteamtools.utils.io.ConfigPaths;
import net.buildtheearth.buildteamtools.utils.io.ConfigUtil;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * Manages all things related to universal tpll
 */
public class NavigationModule extends Module {


    @Getter
    private WarpsComponent warpsComponent;
    @Getter
    private NavigatorComponent navigatorComponent;
    @Getter
    private TpllComponent tpllComponent;
    @Getter
    private BluemapComponent bluemapComponent;
    @Getter
    @Nullable
    private RgcHandler rgcHandler = null;

    private static NavigationModule instance = null;

    public NavigationModule() {
        super("Navigation", WikiLinks.NAV, NetworkModule.getInstance());
    }

    public static NavigationModule getInstance() {
        return instance == null ? instance = new NavigationModule() : instance;
    }


    @Override
    public void enable() {
        if (NetworkModule.getInstance().getBuildTeam() == null) {
            shutdown("The Network Module failed to load the Build Team.");
            return;
        }

        warpsComponent = new WarpsComponent();
        navigatorComponent = new NavigatorComponent();
        tpllComponent = new TpllComponent();

        var navConfig = BuildTeamTools.getInstance().getConfig(ConfigUtil.NAVIGATION);

        if (navConfig.getBoolean(ConfigPaths.Navigation.RGC_LOCAL_DB_ENABLED, false)) {
            File rgcFile = BuildTeamTools.getInstance().getDataPath().resolve("modules/navigation").resolve(navConfig.getString(ConfigPaths.Navigation.RGC_LOCAL_DB_PATH, "bs.file")).toFile();
            ChatHelper.logDebug("Reverse Geocode local database support is enabled. Checking for local database file at: %s", rgcFile.getAbsolutePath());
            if (rgcFile.exists()) {
                rgcHandler = new RgcHandler(rgcFile, BuildTeamTools.getInstance().getSLF4JLogger(), false);
            } else {
                BuildTeamTools.getInstance().getComponentLogger().info("Reverse Geocode local database is enabled but the file does not exist at the specified path, installing it from the configured url.");
                Bukkit.getScheduler().runTaskAsynchronously(BuildTeamTools.getInstance(), () -> {
                    try {
                        if (!rgcFile.getParentFile().mkdirs()) {
                            BuildTeamTools.getInstance().getComponentLogger().warn("Failed to create parent directories for Reverse Geocode local database file. Make sure the plugin has the necessary permissions to create directories and files in the plugin data folder.");
                        }
                        URL url = URI.create(navConfig.getString(ConfigPaths.Navigation.RGC_LOCAL_DB_UPDATE_URL, "")).toURL();
                        try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream())) {
                            try (FileOutputStream fileOutputStream = new FileOutputStream(rgcFile)) {
                                FileChannel fileChannel = fileOutputStream.getChannel();
                                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                            }
                        }
                        Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), () -> {
                            rgcHandler = new RgcHandler(rgcFile, BuildTeamTools.getInstance().getSLF4JLogger(), false);
                            BuildTeamTools.getInstance().getComponentLogger().info("Successfully downloaded Reverse Geocode local database and enabled local database support for Reverse Geocoding.");
                        });
                    } catch (Exception e) {
                        BuildTeamTools.getInstance().getComponentLogger().error("Failed to download Reverse Geocode local database from the configured url, disabling local database support for Reverse Geocoding.", e);
                        navConfig.set(ConfigPaths.Navigation.RGC_LOCAL_DB_ENABLED, false);
                    }
                });
            }
        }

        // Check if BlueMap plugin is enabled and config allows BlueMap integration
        boolean bluemapConfigEnabled = navConfig.getBoolean(ConfigPaths.Navigation.BLUEMAP_ENABLED, true);

        if (Bukkit.getPluginManager().isPluginEnabled("BlueMap") && bluemapConfigEnabled) {
            bluemapComponent = new BluemapComponent();
        }

        if (BuildTeamTools.getInstance().getConfig(ConfigUtil.NAVIGATION).getBoolean(ConfigPaths.Navigation.NAVIGATOR_ITEM_ENABLED, false)) {
            registerListeners(new NavigatorJoinListener(), new NavigatorOpenListener());
        }

        super.enable();
    }

    @Override
    public void registerCommands() {
        registerCommand("warp", new WarpCommand());
        registerCommand("navigator", new NavigatorCommand());
        registerCommand("buildteam", new BuildteamCommand());
        registerCommand("warpsbt", new WarpsBtCommand());
        registerCommand("explore", new ExploreCommand());
    }

    @Override
    public void registerListeners() {
        super.registerListeners(
                new TpllJoinListener(),
                new TpllListener(),
                new WarpJoinListener()
        );
    }
}
