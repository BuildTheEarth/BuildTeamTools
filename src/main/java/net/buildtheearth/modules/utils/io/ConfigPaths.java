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
package net.buildtheearth.modules.utils.io;

public abstract class ConfigPaths {

    // General Behaviour
    public static final String LANGUAGE = "language";
    public static final String API_KEY = "api-key";


    // Navigator.Item
    private static final String NAVIGATOR_ITEM = "navigator-hotbar-item.";

    public static final String NAVIGATOR_ITEM_ENABLED = NAVIGATOR_ITEM + "enabled";
    public static final String NAVIGATOR_ITEM_SLOT = NAVIGATOR_ITEM + "slot";

    // Navigator.MainMenuItems
    private static final String NAVIGATOR_MAIN_MENU = "main_menu_items.";
    private static final String BUILD_ITEM = NAVIGATOR_MAIN_MENU + "build-item.";
    public static final String BUILD_ITEM_ENABLED = BUILD_ITEM + "enabled";
    public static final String BUILD_ITEM_ACTION = BUILD_ITEM + "action";

    private static final String TUTORIALS_ITEM = NAVIGATOR_MAIN_MENU + "tutorial-item.";
    public static final String TUTORIALS_ITEM_ENABLED = TUTORIALS_ITEM + "enabled";
    public static final String TUTORIALS_ITEM_ACTION = TUTORIALS_ITEM + "action";



    // Earth World
    private static final String EARTH_WORLD = "earth-world";

}