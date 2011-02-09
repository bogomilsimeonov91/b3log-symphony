/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.b3log.symphony.util;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.utils.SystemProperty.Environment.Value;
import java.util.ResourceBundle;

/**
 * Symphony utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 9, 2011
 */
public final class Symphonys {

    /**
     * Configurations.
     */
    private static final ResourceBundle CFG =
            ResourceBundle.getBundle("symphony");
    /**
     * GAE environment.
     */
    private static final Value GAE_ENV = SystemProperty.environment.value();

    /**
     * Does Symphony runs on development environment?
     *
     * @return {@code true} if it runs on development environment,
     * {@code false} otherwise
     */
    public static boolean runsOnDevEnv() {
        return SystemProperty.Environment.Value.Development == GAE_ENV;
    }

    /**
     * Gets a configuration property with the specified key.
     *
     * @param key the specified key
     * @return property value corresponding to the specified key, returns
     * {@code null} if not found
     */
    public static String get(final String key) {
        return CFG.getString(key);
    }

    /**
     * Private default constructor.
     */
    private Symphonys() {
    }
}
