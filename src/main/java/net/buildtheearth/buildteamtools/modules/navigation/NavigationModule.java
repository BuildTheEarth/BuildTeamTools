package net.buildtheearth.buildteamtools.modules.navigation;

import com.alpsbte.alpslib.geo.rgc.RgcHandler;
import com.alpsbte.alpslib.utils.ChatHelper;
import lombok.Getter;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.navigation.components.address.commands.AddressCommand;
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
import net.buildtheearth.buildteamtools.utils.io.NavigationConfig;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;

/**
 * Manages all things related to universal tpll
 */
@Getter
public class NavigationModule extends Module {


    private WarpsComponent warpsComponent;
    private NavigatorComponent navigatorComponent;
    private TpllComponent tpllComponent;
    private BluemapComponent bluemapComponent;
    @Nullable
    private RgcHandler rgcHandler = null;

    @Getter
    private static NavigationModule instance = null;
    private final NavigationConfig config;

    public NavigationModule(NavigationConfig config) {
        super("Navigation", WikiLinks.NAV, NetworkModule.getInstance());
        this.config = config;
        instance = this;
    }


    @Override
    public void enable() {
        if (NetworkModule.getInstance().getBuildTeam() == null) {
            shutdown("The Network Module failed to load the Build Team.");
            return;
        }

        warpsComponent = new WarpsComponent();
        navigatorComponent = new NavigatorComponent(config);
        tpllComponent = new TpllComponent();

        initializeRgcHandler(config);
        initializeBluemapComponent(config);

        if (config.navigatorHotbarItem().navEnabled()) {
            registerListeners(new NavigatorOpenListener());
        }

        registerListeners(new NavigatorJoinListener(navigatorComponent, config));

        super.enable();
    }

    private void initializeRgcHandler(@NonNull NavigationConfig navConfig) {
        if (!navConfig.reverseGeocode().localDatabase().enabled()) {
            return;
        }

        File rgcFile = resolveRgcDatabaseFile(navConfig);
        ChatHelper.logDebug("Reverse Geocode local database support is enabled. Checking for local database file at: %s", rgcFile.getAbsolutePath());

        if (rgcFile.exists()) {
            rgcHandler = createRgcHandler(rgcFile);
            return;
        }

        downloadRgcDatabaseAsync(rgcFile, navConfig);
    }

    private @NonNull File resolveRgcDatabaseFile(@NonNull NavigationConfig navConfig) {
        String path = navConfig.reverseGeocode().localDatabase().path();
        return BuildTeamTools.getInstance().getDataPath()
                .resolve("modules/navigation")
                .resolve(path)
                .toFile();
    }

    @Contract("_ -> new")
    private @NonNull RgcHandler createRgcHandler(File rgcFile) {
        return new RgcHandler(rgcFile, BuildTeamTools.getInstance().getSLF4JLogger(), false);
    }

    private void downloadRgcDatabaseAsync(File rgcFile, NavigationConfig navConfig) {
        BuildTeamTools.getInstance().getComponentLogger().info(
                "Reverse Geocode local database is enabled but the file does not exist at the specified path, installing it from the configured url.");
        Bukkit.getScheduler().runTaskAsynchronously(BuildTeamTools.getInstance(), () -> {
            try {
                downloadRgcDatabase(rgcFile, navConfig);
                Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), () -> {
                    rgcHandler = createRgcHandler(rgcFile);
                    BuildTeamTools.getInstance().getComponentLogger().info(
                            "Successfully downloaded Reverse Geocode local database and enabled local database support for Reverse Geocoding.");
                });
            } catch (Exception e) {
                try {
                    Files.deleteIfExists(rgcFile.toPath());
                } catch (IOException cleanupException) {
                    BuildTeamTools.getInstance().getComponentLogger().warn(
                            "Failed to remove the incomplete Reverse Geocode database after download failure.", cleanupException);
                }
                BuildTeamTools.getInstance().getComponentLogger().error(
                        "Failed to download the Reverse Geocode local database from the configured URL. " +
                                "Reverse Geocoding local database support is unavailable for this session.", e);
            }
        });
    }

    private void downloadRgcDatabase(@NonNull File rgcFile, NavigationConfig navConfig) throws IOException {
        if (!rgcFile.getParentFile().mkdirs()) {
            BuildTeamTools.getInstance().getComponentLogger().warn(
                    "Failed to create parent directories for Reverse Geocode local database file. Make sure the plugin has the necessary permissions to create directories and files in the plugin data folder.");
        }
        URL url = URI.create(navConfig.reverseGeocode().localDatabase().url()).toURL();
        try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(rgcFile)) {
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
    }

    private void initializeBluemapComponent(@NonNull NavigationConfig navConfig) {
        boolean bluemapConfigEnabled = navConfig.bluemap().enabled();
        if (Bukkit.getPluginManager().isPluginEnabled("BlueMap") && bluemapConfigEnabled) {
            bluemapComponent = new BluemapComponent(navConfig);
        }
    }

    @Override
    public void registerCommands() {
        registerCommand("warp", new WarpCommand());
        registerCommand("navigator", new NavigatorCommand());
        registerCommand("buildteam", new BuildteamCommand());
        registerCommand("warpsbt", new WarpsBtCommand());
        registerCommand("explore", new ExploreCommand());
        registerCommand("address", new AddressCommand());
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
