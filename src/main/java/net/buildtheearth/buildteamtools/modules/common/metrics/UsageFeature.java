package net.buildtheearth.buildteamtools.modules.common.metrics;

/**
 * Features whose invocations are reported to bStats.
 * <p>
 * Plot system and the sign text generator have no live player entry point, so they are not listed here.
 * Whether those modules stay enabled is covered by the enabled-modules chart.
 */
public enum UsageFeature {
    HOUSE("house"),
    ROAD("road"),
    RAIL("rail"),
    TREE("tree"),
    FIELD("field"),
    KML("kml"),
    NAVIGATOR("navigator"),
    WARPS("warps"),
    TPLL("tpll"),
    ADDRESS("address"),
    MAP("map"),
    STATS("stats"),
    BLOCK_PALETTE("block_palette");

    private final String chartId;

    UsageFeature(String chartId) {
        this.chartId = chartId;
    }

    public String chartId() {
        return chartId;
    }
}
