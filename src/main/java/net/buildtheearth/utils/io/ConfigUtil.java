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

import com.alpsbte.alpslib.io.config.ConfigNotImplementedException;
import com.alpsbte.alpslib.io.config.ConfigurationUtil;

import java.nio.file.Paths;

public enum ConfigUtil {
    MAIN,
    PLOTSYSTEM,
    NAVIGATION;

    private static ConfigurationUtil configUtilInstance;

    public static void init() throws ConfigNotImplementedException {
        if (configUtilInstance != null) return;

        configUtilInstance = new ConfigurationUtil(new ConfigurationUtil.ConfigFile[]{
                new ConfigurationUtil.ConfigFile(Paths.get("config.yml"), 1.4, false),
                new ConfigurationUtil.ConfigFile(Paths.get("modules", "plotsystem", "config.yml"), 1.6, false),
                new ConfigurationUtil.ConfigFile(Paths.get("modules", "navigation", "config.yml"), 1.6, false),
        });
    }

    public static ConfigurationUtil getInstance() {
        return configUtilInstance;
    }
}
