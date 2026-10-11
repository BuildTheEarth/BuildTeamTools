package net.buildtheearth.buildteamtools.utils.io;

import space.arim.dazzleconf.engine.Comments;
import space.arim.dazzleconf.engine.liaison.IntegerRange;
import space.arim.dazzleconf.engine.liaison.SubSection;

@Comments("BuildTeamTools Navigation Module Configuration")
@Comments("[Github Repo]           https://github.com/BuildTheEarth/BuildTeamTools")
@Comments("[Config Documentation]  https://resources.buildtheearth.net/s/btt/doc/configuration-tpEHSZ6Zt2#h-navigation")
@Comments("[Contacts - Discord]    BTE Development Hub, @Zoriot")
public interface NavigationConfig {

    @Comments("Configuration for the navigator item in the player's hotbar.")
    default @SubSection NavigatorHotbarItem navigatorHotbarItem() {
        return new NavigatorHotbarItem() {};
    }

    @Comments("Configuration for the items displayed in the main navigation menu. If all items are disabled, the menu is empty.")
    default @SubSection MainMenuItems mainMenuItems() {
        return new MainMenuItems() {};
    }

    @Comments("Configuration for warp group sorting.")
    default @SubSection Warps warps() {
        return new Warps() {};
    }

    @Comments("Configuration for the BlueMap integration.")
    default @SubSection Bluemap bluemap() {
        return new Bluemap() {};
    }

    @Comments("Configuration for the local reverse geocoding database.")
    default @SubSection ReverseGeocode reverseGeocode() {
        return new ReverseGeocode() {};
    }

    interface NavigatorHotbarItem {
        @Comments("Enable or disable giving the navigator item to players when they join.")
        default boolean navEnabled() { return true; }

        @Comments("Hotbar slot where the navigator item is placed (0-8, where 0 is the first slot).")
        default @IntegerRange(min = 0, max = 8) int navSlot() { return 0; }
    }

    interface MainMenuItems {
        @Comments("Build menu item settings.")
        default @SubSection MenuItem buildItem() { return new MenuItem() {}; }

        @Comments("PlotSystem menu item settings (enabled by default).")
        default @SubSection MenuItem plotsystemItem() {
            return new MenuItem() {
                @Override
                public boolean enabled() { return true; }
            };
        }

        @Comments("Explore menu item settings (enabled by default).")
        default @SubSection MenuItem exploreItem() {
            return new MenuItem() {
                @Override
                public boolean enabled() { return true; }
            };
        }

        @Comments("Tutorial menu item settings.")
        default @SubSection MenuItem tutorialItem() { return new MenuItem() {}; }
    }

    interface MenuItem {
        @Comments("Enable or disable this menu item.")
        default boolean enabled() { return false; }

        @Comments("Command or action executed when this menu item is clicked.")
        default String action() { return "/command"; }
    }

    interface Warps {
        @Comments("Sorting mode for warp groups. Use 'name' to sort alphabetically.")
        default String sortingMode() { return "default"; }
    }

    interface Bluemap {
        @Comments("Enable or disable the BlueMap integration.")
        default boolean enabled() { return true; }
    }

    interface ReverseGeocode {
        @Comments("Local reverse geocoding database settings.")
        default @SubSection LocalDatabase localDatabase() { return new LocalDatabase() {}; }
    }

    interface LocalDatabase {
        @Comments("Enable or disable the local reverse geocoding database.")
        default boolean enabled() { return true; }

        @Comments("URL used to download the reverse geocoding database.")
        default String url() { return "https://data.ub.uni-muenchen.de/61/8/osm-20151130-0.001-2.bin"; }

        @Comments("Path of the reverse geocoding database relative to the navigation module folder.")
        default String path() { return "reversegeocode/osm-20151130-0.001-2.bin"; }
    }
}
