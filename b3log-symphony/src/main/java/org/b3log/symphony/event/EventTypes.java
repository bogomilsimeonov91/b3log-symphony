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

package org.b3log.symphony.event;

/**
 * Event types.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 21, 2010
 */
public final class EventTypes {

    /**
     * Indicates a add article event.
     */
    public static final String ADD_ARTICLE = "Add Article";
    /**
     * Indicates an add comment to article event.
     */
    public static final String ADD_COMMENT_TO_ARTICLE = "Add Comment To Article";

    /**
     * Private default constructor.
     */
    private EventTypes() {
    }
}
