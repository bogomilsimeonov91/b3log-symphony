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
 * This class defines all tag model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 28, 2011
 */
public final class Tag {

    /**
     * Tag.
     */
    public static final String TAG = "tag";
    /**
     * Tags.
     */
    public static final String TAGS = "tags";
    /**
     * Key of title.
     */
    public static final String TAG_TITLE = "tagTitle";
    /**
     * Key of title in lower case.
     */
    public static final String TAG_TITLE_LOWER_CASE = "tagTitleLowerCase";
    /**
     * Key of tag reference count.
     */
    public static final String TAG_REFERENCE_COUNT = "tagReferenceCount";
    /**
     * Key of tag comment count.
     */
    public static final String TAG_COMMENT_COUNT = "tagCommentCount";
    /**
     * Key of tag refereed articles.
     */
    public static final String TAG_ARTICLES_REF = "tagArticles";
    /**
     * Key of tag top authors.
     */
    public static final String TAG_TOP_AUTHORS_REF = "tagTopAuthors";

    /**
     * Private default constructor.
     */
    private Tag() {
    }
}
