package net.buildtheearth.buildteamtools.modules.navigation;

import lombok.Getter;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.navigation.components.bluemap.BluemapComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.navigator.NavigatorComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands.BuildteamCommand;
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
import org.bukkit.Bukkit;

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


    private static NavigationModule instance = null;

    public NavigationModule() {
        super("Navigation", WikiLinks.NAV, NetworkModule.getInstance());
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
        if (Bukkit.getPluginManager().isPluginEnabled("BlueMap")) bluemapComponent = new BluemapComponent();

        super.enable();
    }

    @Override
    public void registerCommands() {
        registerCommand("warp", new WarpCommand());
        registerCommand("navigator", new NavigatorCommand());
        registerCommand("buildteam", new BuildteamCommand());
        registerCommand("warpsbt", new WarpsBtCommand());
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
