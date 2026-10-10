package net.buildtheearth.buildteamtools.utils.io;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import space.arim.dazzleconf.Configuration;
import space.arim.dazzleconf.LoadResult;
import space.arim.dazzleconf.ReloadShell;
import space.arim.dazzleconf.StandardErrorPrint;
import space.arim.dazzleconf.backend.Backend;
import space.arim.dazzleconf.backend.CommentData;
import space.arim.dazzleconf.backend.DataTree;
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
     * Reloads the configuration delegate and removes the obsolete config-version entry and its attached comments.
     * Existing references returned by {@link #getConfig()} see the new values.
     */
    public void reload() {
        try {
            Files.createDirectories(dataFolder);
            Path configFile = dataFolder.resolve("config.yml");
            YamlBackend backend = new YamlBackend(new PathRoot(configFile));
            LoadResult<C> result = configuration.configureWith(backend);
            if (result.isFailure()) {
                new StandardErrorPrint(output -> logger.warn("Failed to load configuration: {}", output.printString()))
                        .onError(result.getErrorContexts());
                reloadShell.setCurrentDelegate(configuration.loadDefaults());
                return;
            }
            removeLegacyConfigVersion(backend);
            reloadShell.setCurrentDelegate(result.getOrThrow());
        } catch (IOException | RuntimeException ex) {
            logger.error("Failed to load configuration from {}", dataFolder.resolve("config.yml"), ex);
            reloadShell.setCurrentDelegate(configuration.loadDefaults());
        }
    }

    private void removeLegacyConfigVersion(@NonNull YamlBackend backend) {
        Backend.Document document = backend.read(configuration.makeErrorSource()).getOrThrow();
        if (document == null) {
            return;
        }
        DataTree.Mut data = document.data().intoMut();
        if (data.remove("config-version") == null) {
            return;
        }
        backend.write(new Backend.Document() {
            @Override
            public @NonNull CommentData comments() {
                return document.comments();
            }

            @Override
            public @NonNull DataTree data() {
                return data;
            }
        });
    }

    /**
     * Returns a reloadable configuration view.
     */
    public C getConfig() {
        return reloadShell.getShell();
    }

}
