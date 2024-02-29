package net.buildtheearth.modules.navigation;

import lombok.Getter;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.navigation.components.navigator.NavigatorComponent;
import net.buildtheearth.modules.navigation.components.navigator.commands.NavigatorCommand;
import net.buildtheearth.modules.navigation.components.navigator.listeners.NavigatorOpenListener;
import net.buildtheearth.modules.navigation.components.tpll.TpllComponent;
import net.buildtheearth.modules.navigation.components.tpll.listeners.TpllJoinListener;
import net.buildtheearth.modules.navigation.components.tpll.listeners.TpllListener;
import net.buildtheearth.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.modules.navigation.components.warps.commands.WarpCommand;
import net.buildtheearth.modules.navigation.components.warps.listeners.WarpJoinListener;
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


    private static NavigationModule instance = null;

    public NavigationModule() {
        super("Navigation");
    }

    public static NavigationModule getInstance() {
        return instance == null ? instance = new NavigationModule() : instance;
    }


    @Override
    public void onEnable() {
        warpsComponent = new WarpsComponent();
        navigatorComponent = new NavigatorComponent();
        tpllComponent = new TpllComponent();

        super.onEnable();
    }

    @Override
    public void registerCommands() {
        BuildTeamTools.getInstance().getCommand("warp").setExecutor(new WarpCommand());
        BuildTeamTools.getInstance().getCommand("navigator").setExecutor(new NavigatorCommand());
    }

    @Override
    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new TpllJoinListener(), BuildTeamTools.getInstance());
        Bukkit.getPluginManager().registerEvents(new TpllListener(), BuildTeamTools.getInstance());

        Bukkit.getPluginManager().registerEvents(new WarpJoinListener(), BuildTeamTools.getInstance());

        Bukkit.getPluginManager().registerEvents(new NavigatorOpenListener(), BuildTeamTools.getInstance());
    }
}
