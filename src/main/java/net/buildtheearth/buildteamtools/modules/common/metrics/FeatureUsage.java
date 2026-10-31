package net.buildtheearth.buildteamtools.modules.common.metrics;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Counts feature invocations since the last metrics submission.
 * Call {@link #record(UsageFeature)} from the player action. {@link #drain()} is for the bStats callback.
 */
public final class FeatureUsage {

    private static final EnumMap<UsageFeature, AtomicInteger> COUNTS = new EnumMap<>(UsageFeature.class);

    static {
        for (UsageFeature feature : UsageFeature.values())
            COUNTS.put(feature, new AtomicInteger());
    }

    private FeatureUsage() {
    }

    public static void record(UsageFeature feature) {
        COUNTS.get(feature).incrementAndGet();
    }

    /**
     * Returns every feature, including those with zero uses, then resets the counters.
     */
    static Map<String, Integer> drain() {
        Map<String, Integer> snapshot = new LinkedHashMap<>();
        for (UsageFeature feature : UsageFeature.values())
            snapshot.put(feature.chartId(), COUNTS.get(feature).getAndSet(0));
        return snapshot;
    }
}
