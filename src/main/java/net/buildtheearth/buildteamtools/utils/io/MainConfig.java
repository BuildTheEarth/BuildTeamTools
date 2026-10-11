package net.buildtheearth.buildteamtools.utils.io;

import space.arim.dazzleconf.engine.Comments;

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
}
