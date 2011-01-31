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
 * This class defines all article model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jan 30, 2011
 */
public final class Article {

    /**
     * Article.
     */
    public static final String ARTICLE = "article";
    /**
     * Articles.
     */
    public static final String ARTICLES = "articles";
    /**
     * Key of title.
     */
    public static final String ARTICLE_TITLE = "articleTitle";
    /**
     * Key of content.
     */
    public static final String ARTICLE_CONTENT = "articleContent";
    /**
     * Key of create date.
     */
    public static final String ARTICLE_CREATE_DATE = "articleCreateDate";
    /**
     * Key of the latest comment date.
     */
    public static final String ARTICLE_LATEST_CMT_DATE = "articleLatestCmtDate";
    /**
     * Key of tags.
     */
    public static final String ARTICLE_TAGS = "articleTags";
    /**
     * Key of comment count.
     */
    public static final String ARTICLE_COMMENT_COUNT = "articleCommentCount";
    /**
     * Key of comments.
     */
    public static final String ARTICLE_COMMENTS_REF = "articleComments";
    /**
     * Key of permalink.
     */
    public static final String ARTICLE_PERMALINK = "articlePermalink";
    /**
     * Key of author name.
     */
    public static final String ARTICLE_AUTHOR_NAME_REF = "articleAuthorName";
    /**
     * Key of author email.
     */
    public static final String ARTICLE_AUTHOR_EMAIL_REF = "articleAuthorEmail";
    /**
     * Key of author id.
     */
    public static final String ARTICLE_AUTHOR_ID = "articleAuthorId";
    /**
     * Key of random double.
     */
    public static final String ARTICLE_RANDOM_DOUBLE =
            "articleRandomDouble";
    /**
     * Date format(yyyy/MM/dd hh:mm:ss).
     */
    public static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /**
     * Private default constructor.
     */
    private Article() {
    }
}
