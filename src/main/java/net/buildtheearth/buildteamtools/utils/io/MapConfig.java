package net.buildtheearth.buildteamtools.utils.io;

import space.arim.dazzleconf.engine.Comments;

@Comments("BuildTeamTools Map Module Configuration")
@Comments("[Github Repo]           https://github.com/BuildTheEarth/BuildTeamTools")
@Comments("[Contacts - Discord]    BTE Development Hub, @Zoriot")
public interface MapConfig {

    @Comments("Build Team ID used by the web API to create map claims.")
    @Comments("This is independent of the Build Team ID configured for NWAPI.")
    default String buildTeamId() {
        return "";
    }

    @Comments("Authentication secret used by the web API client to create map claims.")
    @Comments("Keep this private; claim creation is unavailable when this is empty.")
    default String webApiToken() {
        return "";
    }

    @Comments("Base URL of the BuildTheEarth web API (no trailing slash), e.g. https://api.buildtheearth.net/api/v1")
    default String webApiBasePath() {
        return "https://api.buildtheearth.net/api/v1";
    }
}
