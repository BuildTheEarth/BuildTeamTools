package net.buildtheearth.buildteamtools.modules.common.commands;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpMigrationSource;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.io.MainConfig;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.exception.ArgumentParseException;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.exception.NoPermissionException;
import org.incendo.cloud.exception.NoSuchCommandException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.internal.CommandRegistrationHandler;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class BttCommandManagerTest {
    private TestManager manager;
    private BttCommandManager commands;
    private CommandSourceStack sender;

    @BeforeEach
    void setUp() {
        manager = new TestManager();
        commands = new BttCommandManager(manager, new MainConfig() {}, Logger.getAnonymousLogger());
        CommandSender bukkitSender = (CommandSender) Proxy.newProxyInstance(CommandSender.class.getClassLoader(),
                new Class<?>[]{CommandSender.class}, (proxy, method, args) -> {
                    throw new UnsupportedOperationException(method.getName());
                });
        sender = (CommandSourceStack) Proxy.newProxyInstance(CommandSourceStack.class.getClassLoader(),
                new Class<?>[]{CommandSourceStack.class}, (proxy, method, args) -> {
                    if (method.getName().equals("getSender")) return bukkitSender;
                    throw new UnsupportedOperationException(method.getName());
                });
        commands.registerRoot(null, ignored -> {});
    }

    @Test
    void rootAliasesDoNotCreateDuplicateRoots() {
        commands.registerRootHelp(null);
        commands.registerRootSubcommand(null, "cache", "Show cache", Permissions.BUILD_TEAM_TOOLS_CACHE,
                (sender, label, args) -> {});
        commands.registerRootNestedSubcommand(null, "cache", "update", "Update cache",
                Permissions.BUILD_TEAM_TOOLS_CACHE, ignored -> {});

        assertEquals(1, manager.commandTree().rootNodes().size());
        assertSame(parse("btt help warp").command(), parse("buildteamtools help warp").command());
        assertSame(parse("btt cache update").command(), parse("buildteamtools cache update").command());
    }

    @Test
    void allDefaultFeatureAliasesRegisterWithoutAmbiguity() {
        for (String name : List.of("generate", "kml", "geopoints", "geopath", "georing", "geosurface",
                "warp", "address", "blockpalette")) {
            commands.register(null, name, (sender, args) -> {});
        }
        commands.registerNoArguments(null, "navigator", Permissions.NAVIGATOR_USE, ignored -> {});
        commands.registerNoArguments(null, "explore", Permissions.NAVIGATOR_USE, ignored -> {});
        for (String name : List.of("buildteam", "btwarps")) {
            commands.registerRequiredArgument(null, name, "team", "Build team",
                    StringParser.greedyStringParser(), (sender, team) -> {});
        }

        assertSame(parse("navigator").command(), parse("nav").command());
        assertSame(parse("btt navigator").command(), parse("buildteamtools navigate").command());
        assertSame(parse("buildteam New York").command(), parse("bt New York").command());
        assertEquals("New York", parse("buildteamtools bt New York").<String>get("team"));
        assertEquals("New York", parse("btt btwarp New York").<String>get("team"));
        assertEquals("New York", parse("btwarps New York").<String>get("team"));
        assertParseFailure(NoSuchCommandException.class, "warpsbt New York");
    }

    @Test
    void subcommandsRetainArgumentsPermissionsAndHandlersForEveryAlias() throws Exception {
        AtomicInteger invocations = new AtomicInteger();
        Module owner = new EnabledModule();
        commands.register(null, "warp", (sender, args) -> {});
        commands.registerSubcommand(owner, "warp", "migrate", "Migrate warps",
                builder -> builder.permission(Permissions.WARP_MIGRATE)
                        .required("source", EnumParser.enumParser(WarpMigrationSource.class)),
                ignored -> invocations.incrementAndGet());

        for (String input : List.of("warp migrate essentials", "wp migrate essentials",
                "btt warp migrate essentials", "buildteamtools wp migrate essentials")) {
            var context = parse(input);
            assertEquals(WarpMigrationSource.ESSENTIALS, context.get("source"));
            assertEquals(Permissions.WARP_MIGRATE, context.command().commandPermission().permissionString());
            context.command().commandExecutionHandler().execute(context);
        }
        assertEquals(4, invocations.get());
        assertParseFailure(InvalidSyntaxException.class, "wp migrate");
        assertParseFailure(ArgumentParseException.class, "btt warp migrate invalid");
        manager.deniedPermissions = Set.of(Permissions.WARP_MIGRATE);
        assertParseFailure(NoPermissionException.class, "wp migrate essentials");
        assertParseFailure(NoPermissionException.class, "buildteamtools wp migrate essentials");
    }

    @Test
    void optionalSubcommandArgumentsPreserveTheirDefaultsAndMultiWordInput() {
        commands.register(null, "blockpalette", (sender, args) -> {});
        commands.registerSubcommand(null, "blockpalette", "filter", "Apply filters",
                builder -> builder.optional("filters", StringParser.greedyStringParser(),
                        DefaultValue.constant("")),
                ignored -> {});
        assertEquals("", parse("bp filter").<String>get("filters"));
        assertEquals("color natural", parse("buildteamtools bp filter color natural").<String>get("filters"));
    }

    @Test
    void noArgumentCommandsKeepPermissionsAndRejectExtraInput() {
        commands.registerNoArguments(null, "navigator", Permissions.NAVIGATOR_USE, ignored -> {});
        assertEquals(Permissions.NAVIGATOR_USE,
                parse("nav").command().commandPermission().permissionString());
        assertParseFailure(InvalidSyntaxException.class, "nav unexpected");
        manager.deniedPermissions = Set.of(Permissions.NAVIGATOR_USE);
        assertParseFailure(NoPermissionException.class, "buildteamtools nav");
    }

    @Test
    void requiredAndGreedyCommandsKeepPermissions() {
        commands.registerRequiredArgument(null, "buildteam", "team", "Build team",
                StringParser.greedyStringParser(), Permissions.NAVIGATOR_USE,
                (sender, input) -> List.of(), (sender, team) -> {});
        commands.register(null, "generate", Permissions.GENERATOR_USE,
                (sender, args) -> {}, (sender, input) -> List.of());

        assertParseFailure(InvalidSyntaxException.class, "bt");
        assertEquals(Permissions.GENERATOR_USE,
                parse("g house -w stone").command().commandPermission().permissionString());
        manager.deniedPermissions = Set.of(Permissions.NAVIGATOR_USE, Permissions.GENERATOR_USE);
        assertParseFailure(NoPermissionException.class, "btt bt New York");
        assertParseFailure(NoPermissionException.class, "buildteamtools g house -w stone");
    }

    @Test
    void emptyAliasListsRegisterOnlyTheBttRootAndKeepAllFeatureRoutes() {
        MainConfig.Aliases noAliases = (MainConfig.Aliases) Proxy.newProxyInstance(
                MainConfig.Aliases.class.getClassLoader(), new Class<?>[]{MainConfig.Aliases.class},
                (proxy, method, args) -> List.of());
        manager = new TestManager();
        commands = new BttCommandManager(manager, new MainConfig() {
            @Override
            public Commands commands() {
                return new Commands() {
                    @Override
                    public Aliases aliases() {
                        return noAliases;
                    }
                };
            }
        }, Logger.getAnonymousLogger());
        commands.registerRoot(null, ignored -> {});
        commands.registerRootHelp(null);
        for (String name : List.of("generate", "kml", "geopoints", "geopath", "georing", "geosurface",
                "warp", "address", "blockpalette")) {
            commands.register(null, name, (sender, args) -> {});
            parse("btt " + name);
            assertParseFailure(NoSuchCommandException.class, name);
        }
        for (String name : List.of("navigator", "explore")) {
            commands.registerNoArguments(null, name, ignored -> {});
            parse("btt " + name);
            assertParseFailure(NoSuchCommandException.class, name);
        }
        for (String name : List.of("buildteam", "btwarps")) {
            commands.registerRequiredArgument(null, name, "team", "Build team",
                    StringParser.greedyStringParser(), (sender, team) -> {});
            assertEquals("New York", parse("btt " + name + " New York").<String>get("team"));
            assertParseFailure(NoSuchCommandException.class, name + " New York");
        }
        commands.registerSubcommand(null, "warp", "migrate", "Migrate warps",
                builder -> builder.required("source", EnumParser.enumParser(WarpMigrationSource.class)),
                ignored -> {});
        assertEquals(WarpMigrationSource.ESSENTIALS, parse("btt warp migrate essentials").get("source"));
        assertEquals(1, manager.commandTree().rootNodes().size());
        assertParseFailure(NoSuchCommandException.class, "buildteamtools help");
        assertParseFailure(NoSuchCommandException.class, "wp migrate essentials");
    }

    @Test
    void configuredStandaloneLabelsDoNotImplicitlyIncludeTheCanonicalName() {
        manager = new TestManager();
        commands = new BttCommandManager(manager, new MainConfig() {
            @Override
            public Commands commands() {
                return new Commands() {
                    @Override
                    public Aliases aliases() {
                        return new Aliases() {
                            @Override
                            public List<String> warp() {
                                return List.of("travel");
                            }
                        };
                    }
                };
            }
        }, Logger.getAnonymousLogger());
        commands.registerRoot(null, ignored -> {});
        commands.register(null, "warp", (sender, args) -> {});
        parse("travel New York");
        parse("btt warp New York");
        parse("btt travel New York");
        assertParseFailure(NoSuchCommandException.class, "warp New York");
        assertParseFailure(NoSuchCommandException.class, "wp New York");
    }

    private CommandContext<CommandSourceStack> parse(String input) {
        var context = new CommandContext<>(sender, manager);
        Command<CommandSourceStack> command = manager.commandTree()
                .parse(context, CommandInput.of(input), Runnable::run).join();
        context.command(command);
        return context;
    }

    private void assertParseFailure(Class<? extends Throwable> expected, String input) {
        CompletionException exception = assertThrows(CompletionException.class, () -> parse(input));
        assertInstanceOf(expected, exception.getCause());
    }

    private static final class TestManager extends CommandManager<CommandSourceStack> {
        private Set<String> deniedPermissions = Set.of();

        private TestManager() {
            super(ExecutionCoordinator.simpleCoordinator(),
                    CommandRegistrationHandler.nullCommandRegistrationHandler());
        }

        @Override
        public boolean hasPermission(CommandSourceStack sender, String permission) {
            return !deniedPermissions.contains(permission);
        }
    }

    private static final class EnabledModule extends Module {
        private EnabledModule() {
            super("Test", "");
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void registerListeners() {
        }
    }
}
