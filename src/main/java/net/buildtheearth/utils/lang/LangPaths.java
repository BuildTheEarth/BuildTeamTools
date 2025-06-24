package net.buildtheearth.utils.lang;

public abstract class LangPaths {
    private LangPaths() {}

    public static final class MESSAGE {
        private MESSAGE() {
        }

        private static final String MESSAGE_PREFIX = "message.";
    }

    public static final class ERROR {
        private ERROR() {
        }

        private static final String ERROR_PREFIX = "error.";

        public static final String PLAYER_HAS_NO_PERMISSIONS = ERROR_PREFIX + "player-has-no-permissions";
        public static final String NO_PLAYER = ERROR_PREFIX + "no-player";
    }

    public static final class Common {
        private Common() {
        }

        private static final String COMMON_PREFIX = "common.";

        /** Variables: Module Name, Reason */
        public static final String MODULE_IS_DISABLED = COMMON_PREFIX + "module-is-disabled";
    }

    public static final class Generator {
        private Generator() {
        }

        private static final String GENERATOR_PREFIX = "generator.";

        public static final String NO_WORLDEDIT_WHILE_GENERATING = GENERATOR_PREFIX + "no-worldedit-while-generating";
    }

    public static final class Network {
        private Network() {
        }

        private static final String NETWORK_PREFIX = "network.";

    }

    public static final class Navigator{
        private Navigator() {
        }

        private static final String NAVIGATOR_PREFIX = "navigator.";
    }

    public static final class Plot{
        private Plot(){
        }

        private static final String PLOT_PREFIX = "prefix.";

    }
}