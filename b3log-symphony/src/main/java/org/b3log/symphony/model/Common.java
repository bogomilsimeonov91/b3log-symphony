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

package org.b3log.symphony.model;

/**
 * This class defines all common model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Feb 10, 2011
 */
public final class Common {

    /**
     * Key of from.
     */
    public static final String FROM = "from";
    /**
     * Key of version.
     */
    public static final String VERSION = "version";
    /**
     * Key of host.
     */
    public static final String HOST = "host";
    /**
     * Key of sign.
     */
    public static final String SIGN = "sign";
    /**
     * Key of state.
     */
    public static final String STATE = "state";
    /**
     * Key of author id.
     */
    public static final String AUTHOR_ID = "authorId";
    /**
     * Key of commenter id.
     */
    public static final String COMMENTER_ID = "commenterId";
    /**
     * Available state.
     */
    public static final boolean AVAILABLE = true;
    /**
     * Disabled state.
     */
    public static final boolean DISABLED = false;
    /**
     * Key of user thumbnail file id.
     */
    public static final String USER_THUMBNAIL_FILE_ID = "userThumbnailFileId";
    /**
     * Key of user thumbnail URL.
     */
    public static final String USER_THUMBNAIL_URL = "userThumbnailURL";
    /**
     * Key of tag top users.
     */
    public static final String TAG_TOP_USERS = "tagTopUsers";

    /**
     * Private default constructor.
     */
    private Common() {
    }
}
