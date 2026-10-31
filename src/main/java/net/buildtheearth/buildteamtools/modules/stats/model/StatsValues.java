package net.buildtheearth.buildteamtools.modules.stats.model;

import net.buildtheearth.buildteamtools.modules.stats.StatsModule;

/**
 * Shared numeric accumulation for player/server stats maps.
 */
final class StatsValues {

    private StatsValues() {
    }

    /**
     * @return updated value, or {@code null} when the existing value is already above the rate limit
     */
    static Object accumulate(Object existing, Object value) {
        if (existing == null) {
            return value;
        }
        return switch (existing) {
            case Integer current when value instanceof Integer added ->
                    current > StatsModule.RATE_LIMIT ? null : current + added;
            case Double current when value instanceof Double added ->
                    current > StatsModule.RATE_LIMIT ? null : current + added;
            case Float current when value instanceof Float added ->
                    current > StatsModule.RATE_LIMIT ? null : current + added;
            case Long current when value instanceof Long added ->
                    current > StatsModule.RATE_LIMIT ? null : current + added;
            default -> value;
        };
    }
}
