package net.buildtheearth.buildteamtools.modules.common.metrics;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Counts feature invocations since the last metrics submission.
 * <p>
 * {@link #record(UsageFeature)} counts a feature with no further breakdown.
 * {@link #record(UsageDetail)} counts that part and its parent feature, so the summary charts
 * stay aligned with the drilldown pie.
 */
public final class FeatureUsage {

    private static final EnumMap<UsageFeature, AtomicInteger> COUNTS = new EnumMap<>(UsageFeature.class);
    private static final EnumMap<UsageDetail, AtomicInteger> DETAILS = new EnumMap<>(UsageDetail.class);

    static {
        for (UsageFeature feature : UsageFeature.values())
            COUNTS.put(feature, new AtomicInteger());
        for (UsageDetail detail : UsageDetail.values())
            DETAILS.put(detail, new AtomicInteger());
    }

    private FeatureUsage() {
    }

    public static void record(UsageFeature feature) {
        COUNTS.get(feature).incrementAndGet();
    }

    public static void record(UsageDetail detail) {
        record(detail.feature());
        DETAILS.get(detail).incrementAndGet();
    }

    /**
     * Returns every feature, including those with zero uses, then resets the feature counters.
     */
    static Map<String, Integer> drain() {
        Map<String, Integer> snapshot = new LinkedHashMap<>();
        for (UsageFeature feature : UsageFeature.values())
            snapshot.put(feature.chartId(), COUNTS.get(feature).getAndSet(0));
        return snapshot;
    }

    /**
     * Returns every part of the features that have a breakdown, including zeros, then resets those counters.
     */
    static Map<String, Map<String, Integer>> drainDetails() {
        Map<String, Map<String, Integer>> snapshot = new LinkedHashMap<>();
        for (UsageDetail detail : UsageDetail.values()) {
            snapshot.computeIfAbsent(detail.feature().chartId(), ignored -> new LinkedHashMap<>())
                    .put(detail.chartId(), DETAILS.get(detail).getAndSet(0));
        }
        return snapshot;
    }
}
