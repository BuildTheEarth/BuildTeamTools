package net.buildtheearth.modules.navigation;

import lombok.Getter;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.navigation.components.navigator.NavigatorComponent;
import net.buildtheearth.modules.navigation.components.tpll.TpllComponent;
import net.buildtheearth.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.modules.navigation.components.warps.menu.WarpEditMenu;
import net.buildtheearth.modules.navigation.components.warps.menu.WarpGroupEditMenu;
import net.buildtheearth.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.modules.navigation.components.warps.model.WarpGroup;

/**
 * Manages all things related to universal tpll
 */
public class NavigationModule implements Module {


    @Getter
    private WarpsComponent warpsComponent;

    @Getter
    private NavigatorComponent navigatorComponent;

    @Getter
    private TpllComponent tpllComponent;


    private static NavigationModule instance = null;
    private boolean enabled = false;

    public static NavigationModule getInstance() {
        return instance == null ? instance = new NavigationModule() : instance;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void onEnable() {
        warpsComponent = new WarpsComponent();
        navigatorComponent = new NavigatorComponent();
        tpllComponent = new TpllComponent();

        enabled = true;
    }

    @Override
    public void onDisable() {
        enabled = false;
    }

    @Override
    public String getModuleName() {
        return "Tpll";
    }


}
