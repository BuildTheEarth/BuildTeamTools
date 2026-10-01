package net.buildtheearth.buildteamtools.utils.io;

import space.arim.dazzleconf.engine.Comments;
import space.arim.dazzleconf.engine.liaison.SubSection;

@Comments("BuildTeamTools Generator Module Configuration")
@Comments("[Github Repo]           https://github.com/BuildTheEarth/BuildTeamTools")
@Comments("[Config Documentation]  https://resources.buildtheearth.net/s/btt/doc/configuration-tpEHSZ6Zt2")
@Comments("[Contacts - Discord]    BTE Development Hub, @Zoriot")
public interface GeneratorConfig {

    @Comments("Settings for the rail generator.")
    default @SubSection Rail rail() {
        return new Rail() {};
    }

    interface Rail {
        @Comments("Maximum number of control points accepted for a rail route.")
        default int maxControlPoints() { return 2_000; }

        @Comments("Maximum number of points generated for a rail path.")
        default int maxPathPoints() { return 75_000; }

        @Comments("Maximum number of block placements in one rail generation.")
        default int maxBlockPlacements() { return 300_000; }

        @Comments("Maximum volume of a prepared rail region.")
        default long maxPreparedRegionVolume() { return 6_000_000L; }

        @Comments("Maximum length of any axis in a prepared rail region.")
        default int maxPreparedRegionAxisLength() { return 2_048; }

        @Comments("Number of blocks placed in each batch during generation.")
        default int blockPlacementBatchSize() { return 750; }
    }
}
