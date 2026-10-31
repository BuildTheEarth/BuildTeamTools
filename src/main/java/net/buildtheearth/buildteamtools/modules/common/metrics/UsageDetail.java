package net.buildtheearth.buildteamtools.modules.common.metrics;

/**
 * A part of a {@link UsageFeature} shown when that feature is opened on the bStats drilldown pie.
 */
public enum UsageDetail {
    NAVIGATOR_MENU(UsageFeature.NAVIGATOR, "menu"),
    NAVIGATOR_ITEM(UsageFeature.NAVIGATOR, "item"),
    NAVIGATOR_EXPLORE(UsageFeature.NAVIGATOR, "explore"),
    NAVIGATOR_COUNTRIES(UsageFeature.NAVIGATOR, "countries"),
    NAVIGATOR_STATES(UsageFeature.NAVIGATOR, "states"),
    NAVIGATOR_CITIES(UsageFeature.NAVIGATOR, "cities"),
    NAVIGATOR_BUILD(UsageFeature.NAVIGATOR, "build"),
    NAVIGATOR_PLOTSYSTEM(UsageFeature.NAVIGATOR, "plotsystem"),
    NAVIGATOR_TUTORIALS(UsageFeature.NAVIGATOR, "tutorials"),
    NAVIGATOR_TOGGLE(UsageFeature.NAVIGATOR, "toggle"),
    NAVIGATOR_BUILDTEAM(UsageFeature.NAVIGATOR, "buildteam"),

    WARPS_MENU(UsageFeature.WARPS, "menu"),
    WARPS_TELEPORT(UsageFeature.WARPS, "teleport"),
    WARPS_CREATE(UsageFeature.WARPS, "create"),
    WARPS_MIGRATE(UsageFeature.WARPS, "migrate"),

    ADDRESS_GET(UsageFeature.ADDRESS, "get"),
    ADDRESS_TELEPORT(UsageFeature.ADDRESS, "teleport"),

    KML_GEOPOINTS(UsageFeature.KML, "geopoints"),
    KML_GEOPATH(UsageFeature.KML, "geopath"),
    KML_GEORING(UsageFeature.KML, "georing"),
    KML_GEOSURFACE(UsageFeature.KML, "geosurface"),
    KML_UNDO(UsageFeature.KML, "undo");

    private final UsageFeature feature;
    private final String chartId;

    UsageDetail(UsageFeature feature, String chartId) {
        this.feature = feature;
        this.chartId = chartId;
    }

    public UsageFeature feature() {
        return feature;
    }

    public String chartId() {
        return chartId;
    }
}
