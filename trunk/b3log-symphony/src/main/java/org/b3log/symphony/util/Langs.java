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

import java.util.Collections;
import java.util.Map;
import org.b3log.latke.Latkes;
import org.b3log.latke.service.LangPropsService;

/**
 * Languages utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jan 31, 2011
 */
public final class Langs {

    /**
     * Language service.
     */
    private static final LangPropsService LANG_PROP_SVC =
            LangPropsService.getInstance();
    /**
     * Languages.
     */
    private static final Map<String, String> LANGS;

    static {
        try {
            LANGS = LANG_PROP_SVC.getAll(Latkes.getDefaultLocale());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all language properties.
     *
     * @return language properties
     */
    public static Map<String, String> all() {
        return Collections.unmodifiableMap(LANGS);
    }

    /**
     * Gets the language property with the specified key.
     *
     * @param key the specified key
     * @return property value corresponding to the specified key, returns
     * {@code null} if not found
     */
    public static String get(final String key) {
        return LANGS.get(key);
    }

    /**
     * Private default constructor.
     */
    private Langs() {
    }
}
