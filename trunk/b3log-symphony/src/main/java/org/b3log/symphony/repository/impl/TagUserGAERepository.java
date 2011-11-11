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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.repository.TagUserRepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Tag-User relation Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Nov 11, 2011
 */
public final class TagUserGAERepository extends AbstractRepository
        implements TagUserRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagUserGAERepository.class.getName());

    @Override
    public List<JSONObject> getByUserId(final String userId)
            throws RepositoryException {
        final Query query = new Query().addFilter(User.USER + "_"
                                                  + Keys.OBJECT_ID,
                                                  FilterOperator.EQUAL, userId).
                setPageCount(1);

        try {
            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            return CollectionUtils.jsonArrayToList(array);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public JSONObject getByTagId(final String tagId,
                                 final int currentPageNum,
                                 final int pageSize)
            throws RepositoryException {
        final Query query = new Query().addFilter(Tag.TAG + "_" + Keys.OBJECT_ID,
                                                  FilterOperator.EQUAL, tagId).
                addSort(User.USER + "_" + Keys.OBJECT_ID,
                        SortDirection.DESCENDING).
                setCurrentPageNum(currentPageNum).
                setPageSize(pageSize).
                setPageCount(1);

        return get(query);
    }

    @Override
    public JSONObject getByTagIdAndUserId(final String tagId,
                                          final String userId)
            throws RepositoryException {
        final Query query = new Query().addFilter(User.USER + "_"
                                                  + Keys.OBJECT_ID,
                                                  FilterOperator.EQUAL, userId).
                addFilter(Tag.TAG + "_" + Keys.OBJECT_ID,
                          FilterOperator.EQUAL, tagId).setPageCount(1);

        try {
            final JSONObject result = get(query);
            final JSONArray tags = result.getJSONArray(Keys.RESULTS);
            if (0 == tags.length()) {
                return null;
            }

            return tags.getJSONObject(0);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            throw new RepositoryException(e);
        }
    }

    @Override
    public List<String> getTopTagUsers(final String tagId,
                                       final int fetchSize) {
        final Query query = new Query().addFilter(Tag.TAG + "_" + Keys.OBJECT_ID,
                                                  FilterOperator.EQUAL, tagId).
                addSort(Tag.TAG_REFERENCE_COUNT, SortDirection.DESCENDING).
                setCurrentPageNum(1).setPageSize(fetchSize).setPageCount(1);

        try {
            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            final List<String> ret = new ArrayList<String>();
            for (int i = 0; i < array.length(); i++) {
                final JSONObject tagUser = array.getJSONObject(i);
                ret.add(tagUser.getString(User.USER + "_" + Keys.OBJECT_ID));
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Gets the {@link TagUserGAERepository} singleton.
     *
     * @return the singleton
     */
    public static TagUserGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private TagUserGAERepository(final String name) {
        super(name);
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 23, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final TagUserGAERepository SINGLETON =
                new TagUserGAERepository(Tag.TAG + "_" + User.USER);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
