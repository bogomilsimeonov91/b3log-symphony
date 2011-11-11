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
package org.b3log.symphony.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.symphony.model.Comment;
import org.b3log.symphony.repository.CommentRepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Comment Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Nov 11, 2011
 */
public final class CommentGAERepository extends AbstractRepository
        implements CommentRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CommentGAERepository.class.getName());

    @Override
    public List<JSONObject> getRecentComments(final int num)
            throws RepositoryException {
        final Query query = new Query().addSort(Keys.OBJECT_ID,
                                                SortDirection.DESCENDING).
                setCurrentPageNum(1).
                setPageSize(num).
                setPageCount(1);

        List<JSONObject> ret = new ArrayList<JSONObject>();
        try {
            final JSONObject result = get(query);

            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            ret = CollectionUtils.jsonArrayToList(array);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return ret;
        }

        LOGGER.log(Level.FINEST,
                   "Got the recent comments, then put it into cache");

        return ret;
    }

    /**
     * Gets the {@link CommentGAERepository} singleton.
     *
     * @return the singleton
     */
    public static CommentGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private CommentGAERepository(final String name) {
        super(name);
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final CommentGAERepository SINGLETON =
                new CommentGAERepository(Comment.COMMENT);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
