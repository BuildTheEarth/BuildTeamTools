package net.buildtheearth.buildteamtools.modules.common.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeatureUsageTest {

    @BeforeEach
    void resetMetrics() {
        MetricsManager.resetForTests();
    }

    @Test
    void drainReturnsEveryFeatureAndResetsTheCounters() {
        FeatureUsage.record(UsageFeature.HOUSE);
        FeatureUsage.record(UsageFeature.HOUSE);
        FeatureUsage.record(UsageFeature.KML);

        Map<String, Integer> first = FeatureUsage.drain();
        assertEquals(UsageFeature.values().length, first.size());
        assertEquals(2, first.get("house"));
        assertEquals(1, first.get("kml"));
        assertEquals(0, first.get("field"));
        assertEquals(0, first.get("block_palette"));

        Map<String, Integer> second = FeatureUsage.drain();
        assertEquals(0, second.get("house"));
        assertEquals(0, second.get("kml"));
    }

    @Test
    void bothChartsShareOneSnapshotUntilTheNextSubmission() {
        FeatureUsage.record(UsageFeature.ROAD);

        Map<String, Integer> first = MetricsManager.featureUseCounts(0);
        assertEquals(1, first.get("road"));
        assertEquals(0, first.get("house"));
        assertEquals(0, first.get("bluemap"));
        assertEquals(UsageFeature.values().length + 1, first.size());

        FeatureUsage.record(UsageFeature.TREE);
        Map<String, Integer> reused = MetricsManager.featureUseCounts(TimeUnit.SECONDS.toNanos(30));
        assertEquals(1, reused.get("road"));
        assertEquals(0, reused.get("tree"));

        Map<String, Integer> nextSubmission = MetricsManager.featureUseCounts(TimeUnit.MINUTES.toNanos(2));
        assertEquals(0, nextSubmission.get("road"));
        assertEquals(1, nextSubmission.get("tree"));
        assertEquals(0, nextSubmission.get("bluemap"));
    }

    @Test
    void drilldownCountsThePartAndItsParent() {
        FeatureUsage.record(UsageDetail.NAVIGATOR_EXPLORE);
        FeatureUsage.record(UsageDetail.NAVIGATOR_EXPLORE);
        FeatureUsage.record(UsageDetail.ADDRESS_GET);
        FeatureUsage.record(UsageDetail.KML_UNDO);

        Map<String, Integer> counts = MetricsManager.featureUseCounts(0);
        assertEquals(2, counts.get("navigator"));
        assertEquals(1, counts.get("address"));
        assertEquals(1, counts.get("kml"));
        assertEquals(0, counts.get("warps"));

        Map<String, Map<String, Integer>> details = MetricsManager.featureDetails(0);
        assertEquals(2, details.get("navigator").get("explore"));
        assertEquals(0, details.get("navigator").get("cities"));
        assertEquals(0, details.get("navigator").get("buildteam"));
        assertEquals(1, details.get("address").get("get"));
        assertEquals(0, details.get("address").get("teleport"));
        assertEquals(1, details.get("kml").get("undo"));
        assertEquals(0, details.get("kml").get("geopoints"));
        assertEquals(0, details.get("warps").get("migrate"));
        assertEquals(4, details.size());

        FeatureUsage.record(UsageDetail.WARPS_MENU);
        Map<String, Map<String, Integer>> reused = MetricsManager.featureDetails(TimeUnit.SECONDS.toNanos(30));
        assertEquals(0, reused.get("warps").get("menu"));

        Map<String, Map<String, Integer>> nextSubmission = MetricsManager.featureDetails(TimeUnit.MINUTES.toNanos(2));
        assertEquals(1, nextSubmission.get("warps").get("menu"));
        assertEquals(0, nextSubmission.get("navigator").get("explore"));
    }
}
