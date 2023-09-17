/*
 * The MIT License (MIT)
 *
 *  Copyright © 2023, Alps BTE <bte.atchli@gmail.com>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.buildtheearth.utils.io;

public abstract class ConfigPaths {

    // General Behaviour
    public static final String LANGUAGE = "language";
    public static final String API_KEY = "api-key";



    // Navigator.Item
    private static final String NAVIGATOR_ITEM = "navigator-item.";

    public static final String ITEM_ENABLED = NAVIGATOR_ITEM + "enabled";
    public static final String ITEM_SLOT = NAVIGATOR_ITEM + "slot";

    // Navigator.MainMenuItems
    private static final String NAVIGATOR_MAIN_MENU = "main_menu_items.";
    private static final String BUILD_ITEM = NAVIGATOR_MAIN_MENU + "build.";
    private static final String EXPLORE_ITEM = NAVIGATOR_MAIN_MENU + "explore.";

    private static final String TUTORIALS_ITEM = NAVIGATOR_MAIN_MENU + "tutorials.";

    public static final String BUILD_ITEM_ENABLED = BUILD_ITEM + "enabled";
    public static final String BUILD_ITEM_MATERIAL = BUILD_ITEM + "material";
    public static final String BUILD_ITEM_LORE = BUILD_ITEM + "lore";
    public static final String BUILD_ITEM_COMMAND = BUILD_ITEM + "command";
    public static final String BUILD_ITEM_MESSAGE = BUILD_ITEM + "message";

    public static final String EXPLORE_ITEM_ENABLED = EXPLORE_ITEM + "enabled";
    public static final String EXPLORE_ITEM_MATERIAL = EXPLORE_ITEM + "material";
    public static final String EXPLORE_ITEM_LORE = EXPLORE_ITEM + "lore";
    public static final String EXPLORE_ITEM_COMMAND = EXPLORE_ITEM + "command";
    public static final String EXPLORE_ITEM_MESSAGE = EXPLORE_ITEM + "message";

    public static final String TUTORIALS_ITEM_ENABLED = TUTORIALS_ITEM + "enabled";
    public static final String TUTORIALS_ITEM_MATERIAL = TUTORIALS_ITEM + "material";
    public static final String TUTORIALS_ITEM_LORE = TUTORIALS_ITEM + "lore";
    public static final String TUTORIALS_ITEM_COMMAND = TUTORIALS_ITEM + "command";
    public static final String TUTORIALS_ITEM_MESSAGE = TUTORIALS_ITEM + "message";

    // Navigator.BuildMenuItems
    private static final String NAVIGATOR_BUILD_MENU = "build_menu_items.";
    private static final String BUILD_TUTORIALS_ITEM = NAVIGATOR_BUILD_MENU + "tutorials.";
    private static final String PLOT_SYSTEM_ITEM = NAVIGATOR_BUILD_MENU + "plot_system.";
    private static final String REGIONS_ITEM = NAVIGATOR_BUILD_MENU + "regions.";
    private static final String TOOLS_ITEM = NAVIGATOR_BUILD_MENU + "tools.";

    public static final String BUILD_TUTORIALS_ITEM_ENABLED = BUILD_TUTORIALS_ITEM + "enabled";
    public static final String BUILD_TUTORIALS_ITEM_MATERIAL = BUILD_TUTORIALS_ITEM + "material";
    public static final String PLOT_SYSTEM_ITEM_ENABLED = PLOT_SYSTEM_ITEM + "enabled";
    public static final String PLOT_SYSTEM_ITEM_MATERIAL = PLOT_SYSTEM_ITEM + "material";
    public static final String REGIONS_ITEM_ENABLED = REGIONS_ITEM + "enabled";
    public static final String REGIONS_ITEM_MATERIAL = REGIONS_ITEM + "material";
    public static final String TOOLS_ITEM_ENABLED = TOOLS_ITEM + "enabled";
    public static final String TOOLS_ITEM_MATERIAL = TOOLS_ITEM + "material";


    // Universal TPLL
    private static final String UNIVERSAL_TPLL = "universal_tpll.";
    private static final String GEOGRAPHY = UNIVERSAL_TPLL + "geography.";

    public static final String UNIVERSAL_TPLL_ZOOM = GEOGRAPHY + "zoom";
    public static final String UNIVERSAL_TPLL_USE_OFFLINE_MODE = GEOGRAPHY + "use_offline_mode";
    public static final String UNIVERSAL_TPLL_BORDER_TELEPORTATION = GEOGRAPHY + "border_teleportation";
    public static final String UNIVERSAL_TPLL_API = GEOGRAPHY + "API";
    public static final String UNIVERSAL_TPLL_EARTH_WORLD = GEOGRAPHY + "earth_world";
}