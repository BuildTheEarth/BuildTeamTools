package net.buildtheearth.modules.navigation;

import lombok.Getter;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.navigation.components.navigator.NavigatorComponent;
import net.buildtheearth.modules.navigation.components.navigator.commands.BuildteamCommand;
import net.buildtheearth.modules.navigation.components.navigator.commands.NavigatorCommand;
import net.buildtheearth.modules.navigation.components.navigator.listeners.NavigatorJoinListener;
import net.buildtheearth.modules.navigation.components.navigator.listeners.NavigatorOpenListener;
import net.buildtheearth.modules.navigation.components.tpll.TpllComponent;
import net.buildtheearth.modules.navigation.components.tpll.listeners.TpllJoinListener;
import net.buildtheearth.modules.navigation.components.tpll.listeners.TpllListener;
import net.buildtheearth.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.modules.navigation.components.warps.commands.WarpCommand;
import net.buildtheearth.modules.navigation.components.warps.listeners.WarpJoinListener;
import net.buildtheearth.modules.network.NetworkModule;

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


    private static NavigationModule instance = null;

    public NavigationModule() {
        super("Navigation", NetworkModule.getInstance());
    }

    public static NavigationModule getInstance() {
        return instance == null ? instance = new NavigationModule() : instance;
    }


    @Override
    public void enable() {
        if(NetworkModule.getInstance().getBuildTeam() == null) {
            shutdown("The Network Module failed to load the Build Team.");
            return;
        }

        warpsComponent = new WarpsComponent();
        navigatorComponent = new NavigatorComponent();
        tpllComponent = new TpllComponent();

        super.enable();
    }

    @Override
    public void registerCommands() {
        registerCommand("warp", new WarpCommand());
        registerCommand("navigator", new NavigatorCommand());
        registerCommand("buildteam", new BuildteamCommand());
    }

    @Override
    public void registerListeners() {
        super.registerListeners(
                new TpllJoinListener(),
                new TpllListener(),
                new WarpJoinListener(),
                new NavigatorJoinListener(),
                new NavigatorOpenListener()
        );
    }
}
