package net.buildtheearth.buildteamtools.utils.io;

import space.arim.dazzleconf.engine.Comments;
import space.arim.dazzleconf.engine.liaison.SubSection;

@Comments("Plot System Terra Configuration")
@Comments("[Github Repo]           https://github.com/BuildTheEarth/BuildTeamTools")
@Comments("[Config Documentation]  https://resources.buildtheearth.net/s/btt/doc/configuration-tpEHSZ6Zt2#h-plot-system")
@Comments("[Contacts - Discord]    BTE Development Hub, @Zoriot")
public interface PlotSystemConfig {
    @Comments("Data source used by Plot System Terra. Possible values are API or DATABASE.")
    default String dataMode() { return "API"; }

    @Comments("Database connection settings used when data-mode is DATABASE.")
    default @SubSection Database database() { return new Database() {}; }

    @Comments("Additional environment scanning settings.")
    default @SubSection Environment environment() { return new Environment() {}; }

    @Comments("Name of the server registered in the plot system database.")
    default String serverName() { return "default"; }

    @Comments("Enable fast mode when pasting plots with WorldEdit.")
    default boolean fastMode() { return true; }

    @Comments("Interval in seconds between checks for completed plots.")
    default int pastingInterval() { return 300; }

    @Comments("Broadcast plot placement information to online players.")
    default boolean broadcastInfo() { return true; }

    interface Database {
        @Comments("MariaDB/MySQL connection URL.")
        default String dbUrl() { return "jdbc:mariadb://adress:3306/"; }

        @Comments("Database name.")
        default String dbname() { return "plotsystem"; }

        @Comments("Database username.")
        default String username() { return "user"; }

        @Comments("Database password.")
        default String password() { return "password"; }
    }

    interface Environment {
        @Comments("Enable scanning of the environment around a plot.")
        default boolean enabled() { return true; }

        @Comments("Radius in blocks around the plot to scan.")
        default int radius() { return 50; }
    }
}
