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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import static com.google.appengine.api.datastore.FetchOptions.Builder.*;
import com.google.appengine.api.datastore.QueryResultList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Comment;
import org.b3log.symphony.repository.ArticleCommentRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article-Comment relation Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Jan 12, 2011
 */
public final class ArticleCommentGAERepository extends AbstractGAERepository
        implements ArticleCommentRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleCommentGAERepository.class.getName());

    @Override
    public String getName() {
        return Article.ARTICLE + "_" + Comment.COMMENT;
    }

    @Override
    public JSONObject getByArticleId(final String articleId,
                                           final int currentPageNum,
                                           final int pageSize)
            throws RepositoryException {
       final Query query = new Query(getName());
        query.addFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                        Query.FilterOperator.EQUAL, articleId);
        query.addSort(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                      Query.SortDirection.DESCENDING);

        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final int count = preparedQuery.countEntities(
                FetchOptions.Builder.withDefaults());
        final int pageCount =
                (int) Math.ceil((double) count / (double) pageSize);

        final JSONObject ret = new JSONObject();
        final JSONObject pagination = new JSONObject();
        try {
            ret.put(Pagination.PAGINATION, pagination);
            pagination.put(Pagination.PAGINATION, pagination);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);

            final int offset = pageSize * (currentPageNum - 1);
            final QueryResultList<Entity> queryResultList =
                    preparedQuery.asQueryResultList(
                    withOffset(offset).limit(pageSize));
            final JSONArray results = new JSONArray();
            ret.put(Keys.RESULTS, results);
            for (final Entity entity : queryResultList) {
                final Map<String, Object> properties = entity.getProperties();
                final JSONObject e = new JSONObject(properties);

                results.put(e);
            }
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return ret;
    }

    @Override
    public JSONObject getByCommentId(final String commentId)
            throws RepositoryException {
        final Query query = new Query(getName());
        query.addFilter(Comment.COMMENT + "_" + Keys.OBJECT_ID,
                        Query.FilterOperator.EQUAL, commentId);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);

        final Entity entity = preparedQuery.asSingleEntity();
        if (null == entity) {
            return null;
        }

        final Map<String, Object> properties = entity.getProperties();

        return new JSONObject(properties);
    }

    /**
     * Gets the {@link ArticleCommentGAERepository} singleton.
     *
     * @return the singleton
     */
    public static ArticleCommentGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private ArticleCommentGAERepository() {
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
        private static final ArticleCommentGAERepository SINGLETON =
                new ArticleCommentGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}