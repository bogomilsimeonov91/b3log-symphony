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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * This class defines all comment model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 23, 2011
 */
public final class Comment {

    /**
     * Comment.
     */
    public static final String COMMENT = "comment";
    /**
     * Comments.
     */
    public static final String COMMENTS = "comments";
    /**
     * Key of comment.
     */
    public static final String COMMENT_CONTENT = "commentContent";
    /**
     * Key of comment name.
     */
    public static final String COMMENT_NAME = "commentName";
    /**
     * Key of comment email.
     */
    public static final String COMMENT_EMAIL = "commentEmail";
    /**
     * Key of commenter URL.
     */
    public static final String COMMENTER_URL = "commenterURL";
    /**
     * Key of comment sharp URL.
     */
    public static final String COMMENT_SHARP_URL = "commentSharpURL";
    /**
     * Key of comment date.
     */
    public static final String COMMENT_DATE = "commentDate";
    /**
     * Key of comment thumbnail URL.
     */
    public static final String COMMENT_THUMBNAIL_URL = "commentThumbnailURL";
    /**
     * Key of original comment id.
     */
    public static final String COMMENT_ORIGINAL_COMMENT_ID =
            "commentOriginalCommentId";
    /**
     * Key of original comment user name.
     */
    public static final String COMMENT_ORIGINAL_COMMENT_NAME =
            "commentOriginalCommentName";
    /**
     * Date format(yyyy/MM/dd hh:mm:ss).
     */
    public static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /**
     * Private default constructor.
     */
    private Comment() {
    }
}
