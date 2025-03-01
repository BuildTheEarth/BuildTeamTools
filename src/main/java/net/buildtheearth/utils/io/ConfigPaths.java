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

    public static class PlotSystem {

        // Data Mode
        public static final String DATA_MODE = "data-mode";

        // Database
        private static final String DATABASE = "database.";
        public static final String DATABASE_URL = DATABASE + "db-url";
        public static final String DATABASE_NAME = DATABASE + "dbname";
        public static final String DATABASE_USERNAME = DATABASE + "username";
        public static final String DATABASE_PASSWORD = DATABASE + "password";


        // PLOT SCANNING
        private static final String ENVIRONMENT = "environment.";
        public static final String ENVIRONMENT_ENABLED = ENVIRONMENT + "enabled";
        public static final String ENVIRONMENT_RADIUS = ENVIRONMENT + "radius";


        // PLOT PASTING
        public static final String SERVER_NAME = "server-name";
        public static final String WORLD_NAME = "world-name";
        public static final String FAST_MODE = "fast-mode";
        public static final String PASTING_INTERVAL = "pasting-interval";
        public static final String BROADCAST_INFO = "broadcast-info";


        // FORMATTING
        public static final String MESSAGE_PREFIX = "message-prefix";
        public static final String MESSAGE_INFO_COLOUR = "info-colour";
        public static final String MESSAGE_ERROR_COLOUR = "error-colour";
    }

}