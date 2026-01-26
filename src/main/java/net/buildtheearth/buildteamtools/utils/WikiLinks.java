package net.buildtheearth.buildteamtools.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WikiLinks {
    private static final String WIKI_BASE_URL = "https://resources.buildtheearth.net/s/btt/";
    private static final String WIKI_BASE_URL_DOC = WIKI_BASE_URL + "doc/";
    public static final String ENTRY = WIKI_BASE_URL;
    public static final String GEN = WIKI_BASE_URL_DOC + "generator-module-13zqgI4yFA";
    public static final String NAV = WIKI_BASE_URL_DOC + "sledgehammer-module-T7I0PWPsTD";
    public static final String MISC = WIKI_BASE_URL_DOC + "miscellaneous-appUzeNc5I";
    public static final String STATS = WIKI_BASE_URL_DOC + "statistics-module-iM7IfoKroF";
    public static final String PLOT = "https://github.com/AlpsBTE/Plot-System/wiki/Installation#setting-up-the-terra-servers";

    @UtilityClass
    public static class Gen {
        public static final String RAIL = WIKI_BASE_URL_DOC + "rail-generator-EfgKXdBvk1";
        public static final String ROAD = WIKI_BASE_URL_DOC + "road-generator-QqKBBP0nqO";
        public static final String TREE = WIKI_BASE_URL_DOC + "tree-generator-pnDmYC9hzW";
        public static final String HOUSE = WIKI_BASE_URL_DOC + "house-generator-YKQunon6Bp";
        public static final String FIELD = WIKI_BASE_URL_DOC + "field-generator-OqIN2BrZT7";

        @UtilityClass
        public static class Field {
            private static final String CROP_BASE = WIKI_BASE_URL_DOC + "crop-types-z7ww6RZK2J";
            public static final String CROP_POTATO = CROP_BASE + "#h-potato-field";
            public static final String CROP_CORN = CROP_BASE + "#h-corn-field";
            public static final String CROP_WHEAT = CROP_BASE + "#h-wheat-field";
            public static final String CROP_HARVESTED = CROP_BASE + "#h-harvested-field";
            public static final String CROP_OTHER = CROP_BASE + "#h-other";
            public static final String CROP_VINEYARD = CROP_BASE + "#h-vineyard-field";
            public static final String CROP_PEAR = CROP_BASE + "#h-pear-field";
            public static final String CROP_CATTLE = CROP_BASE + "#h-cattle";
            public static final String CROP_MEADOW = CROP_BASE + "#h-meadow";
        }
    }
}
