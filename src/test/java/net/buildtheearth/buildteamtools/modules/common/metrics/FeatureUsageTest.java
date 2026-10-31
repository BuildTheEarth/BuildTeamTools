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
}
