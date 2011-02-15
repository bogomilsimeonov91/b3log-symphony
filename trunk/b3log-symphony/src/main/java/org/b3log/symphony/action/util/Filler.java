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

package org.b3log.symphony.action.util;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.CommentRepository;
import org.b3log.symphony.repository.TagRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.ArticleGAERepository;
import org.b3log.symphony.repository.impl.CommentGAERepository;
import org.b3log.symphony.repository.impl.TagGAERepository;
import org.b3log.symphony.repository.impl.UserGAERepository;

/**
 * About action. about.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Feb 15, 2011
 */
public final class Filler {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Filler.class.getName());
    /**
     * User repository.
     */
    private static final UserRepository USER_REPOS =
            UserGAERepository.getInstance();
    /**
     * Article repository.
     */
    private static final ArticleRepository ARTICLE_REPOS =
            ArticleGAERepository.getInstance();
    /**
     * Comment repository.
     */
    private static final CommentRepository COMMENT_REPOS =
            CommentGAERepository.getInstance();
    /**
     * Tag repository.
     */
    private static final TagRepository TAG_REPOS =
            TagGAERepository.getInstance();

    /**
     * Fills the specified data model with commons.
     *
     * @param dataModel the specified data model
     */
    public static void fillCommon(final Map<String, Object> dataModel) {
        try {
            dataModel.put(Common.STAT_USER_CNT, USER_REPOS.count());
            dataModel.put(Common.STAT_COMMENT_CNT, COMMENT_REPOS.count());
            dataModel.put(Common.STAT_ARTICLE_CNT, ARTICLE_REPOS.count());
            dataModel.put(Common.STAT_TAG_CNT, TAG_REPOS.count());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            dataModel.put(Common.STAT_USER_CNT, "Unavailable");
            dataModel.put(Common.STAT_COMMENT_CNT, "Unavailable");
            dataModel.put(Common.STAT_ARTICLE_CNT, "Unavailable");
            dataModel.put(Common.STAT_TAG_CNT, "Unavailable");
        }
    }

    /**
     * Private default constructor.
     */
    private Filler() {
    }
}
