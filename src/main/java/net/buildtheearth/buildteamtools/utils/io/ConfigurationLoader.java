package net.buildtheearth.buildteamtools.utils.io;

import org.slf4j.Logger;
import space.arim.dazzleconf.Configuration;
import space.arim.dazzleconf.ReloadShell;
import space.arim.dazzleconf.StandardErrorPrint;
import space.arim.dazzleconf.backend.PathRoot;
import space.arim.dazzleconf.backend.yaml.YamlBackend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Loads and reloads a BuildTeamTools configuration.
 */
public class ConfigurationLoader<C> {

    private final Configuration<C> configuration;
    private final Path dataFolder;
    private final Logger logger;
    private final ReloadShell<C> reloadShell;

    public ConfigurationLoader(Class<C> configType, Path dataFolder, Logger logger) {
        this.configuration = Configuration.defaultBuilder(configType).build();
        this.dataFolder = Objects.requireNonNull(dataFolder, "dataFolder");
        this.logger = Objects.requireNonNull(logger, "logger");
        this.reloadShell = configuration.makeReloadShell(configuration.loadDefaults());
    }

    /**
     * Loads the configuration and creates the file with defaults when it is missing.
     */
    public C load() {
        reload();
        return getConfig();
    }

    /**
     * Reloads the configuration delegate. Existing references returned by {@link #getConfig()} see the new values.
     */
    public void reload() {
        try {
            Files.createDirectories(dataFolder);
            Path configFile = dataFolder.resolve("config.yml");
            C loaded = configuration.configureOrFallback(
                    new YamlBackend(new PathRoot(configFile)),
                    new StandardErrorPrint(output -> logger.warn("Failed to load configuration: {}", output.printString()))
            );
            reloadShell.setCurrentDelegate(loaded);
        } catch (IOException | RuntimeException ex) {
            logger.error("Failed to load configuration from {}", dataFolder.resolve("config.yml"), ex);
            reloadShell.setCurrentDelegate(configuration.loadDefaults());
        }
    }

    /**
     * Returns a reloadable configuration view.
     */
    public C getConfig() {
        return reloadShell.getShell();
    }

}
