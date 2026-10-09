package net.buildtheearth.buildteamtools.utils.io;

import space.arim.dazzleconf.engine.Comments;
import space.arim.dazzleconf.engine.liaison.SubSection;

import java.util.List;

@Comments("BuildTeamTools - by BuildTheEarth")
@Comments("[Github Repo]           https://github.com/BuildTheEarth/BuildTeamTools")
@Comments("[Config Documentation]  https://resources.buildtheearth.net/s/btt/doc/configuration-tpEHSZ6Zt2#h-general-configuration")
@Comments("[Contacts - Discord]    BTE Development Hub, @Zoriot")
public interface MainConfig {


    @Comments("The Build Team API Key used to fetch build team info for display.")
    @Comments("Visit https://resources.buildtheearth.net/s/btt/doc/configuration-tpEHSZ6Zt2#h-api-key-of-the-server")
    @Comments("for an up-to-date guide on how to get your own API key.")
    default String apiKey() {
        return "00000000-0000-0000-0000-000000000000";
    }

    @Comments("The main world using the custom BTE Dynmaxion projection")
    default String earthWorld() {
        return "world";
    }

    @Comments("The modules that should be disabled. E.g. \"Generator\"")
    default List<String> disabledModules() {
        return List.of();
    }

    @Comments("Enable or disable the auto-update on restart feature")
    default boolean autoUpdate() {
        return true;
    }

    @Comments("Enable or disable debug mode")
    default boolean debug() {
        return false;
    }

    @Comments("Command names and aliases. Changes require a server restart.")
    default @SubSection Commands commands() {
        return new Commands() {};
    }

    interface Commands {
        @Comments("Standalone command labels. An empty list disables standalone registration for that feature.")
        @Comments("Canonical /btt subcommands are always registered. Changes require a restart.")
        default @SubSection Aliases aliases() {
            return new Aliases() {};
        }
    }

    interface Aliases {
        @Comments("Aliases are registered at server startup. Restart the server after changing them.")
        default List<String> btt() { return List.of("buildteamtools"); }

        default List<String> generate() { return List.of("generate", "gen", "g"); }
        default List<String> kml() { return List.of("kml"); }
        @Comments("Aliases for the KML points command.")
        default List<String> geopoints() { return List.of("geopoints"); }
        @Comments("Aliases for the KML path command.")
        default List<String> geopath() { return List.of("geopath"); }
        @Comments("Aliases for the KML closed-path command.")
        default List<String> georing() { return List.of("georing"); }
        @Comments("Aliases for the KML filled-surface command.")
        default List<String> geosurface() { return List.of("geosurface"); }
        default List<String> navigator() { return List.of("navigator", "nav", "navigate"); }
        default List<String> buildteam() { return List.of("buildteam", "bt"); }
        default List<String> btwarps() { return List.of("btwarps", "btwarp", "wbt", "wpt", "buildteamwarps"); }
        default List<String> address() { return List.of("address"); }
        default List<String> warp() { return List.of("warp", "warps", "wp"); }
        default List<String> blockpalette() { return List.of("blockpalette", "bp", "blocks"); }
    }
}
