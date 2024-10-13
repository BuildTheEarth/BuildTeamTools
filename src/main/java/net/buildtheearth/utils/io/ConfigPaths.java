package net.buildtheearth.utils.io;

public abstract class ConfigPaths {

    // General Behaviour
    public static final String LANGUAGE = "language";
    public static final String API_KEY = "api-key";

    public static final String DEBUG = "debug";

    // Earth World
    public static final String EARTH_WORLD = "earth-world";




    public static class Navigation {

        // Navigator.Item
        private static final String NAVIGATOR_ITEM = "navigator-hotbar-item.";

        public static final String NAVIGATOR_ITEM_ENABLED = NAVIGATOR_ITEM + "nav-enabled";
        public static final String NAVIGATOR_ITEM_SLOT = NAVIGATOR_ITEM + "nav-slot";

        // Navigator.MainMenuItems
        private static final String NAVIGATOR_MAIN_MENU = "main-menu-items.";
        private static final String BUILD_ITEM = NAVIGATOR_MAIN_MENU + "build-item.";
        public static final String BUILD_ITEM_ENABLED = BUILD_ITEM + "build-enabled";
        public static final String BUILD_ITEM_ACTION = BUILD_ITEM + "build-action";

        private static final String TUTORIALS_ITEM = NAVIGATOR_MAIN_MENU + "tutorial-item.";
        public static final String TUTORIALS_ITEM_ENABLED = TUTORIALS_ITEM + "tutorial-enabled";
        public static final String TUTORIALS_ITEM_ACTION = TUTORIALS_ITEM + "tutorial-action";

    }

}