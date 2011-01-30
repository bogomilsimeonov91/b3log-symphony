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
import com.google.appengine.api.datastore.QueryResultIterable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.RunsOnEnv;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.TagArticleRepository;
import org.b3log.symphony.repository.TagRepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Tag Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jan 30, 2011
 */
public final class TagGAERepository extends AbstractGAERepository
        implements TagRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagGAERepository.class.getName());
    /**
     * Cache.
     */
    private static final Cache<String, Object> CACHE;
    /**
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository =
            TagArticleGAERepository.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();

    static {
        final RunsOnEnv runsOnEnv = Latkes.getRunsOnEnv();
        if (!runsOnEnv.equals(RunsOnEnv.GAE)) {
            throw new RuntimeException(
                    "GAE repository can only runs on Google App Engine, please "
                    + "check your configuration and make sure "
                    + "Latkes.setRunsOnEnv(RunsOnEnv.GAE) was invoked before "
                    + "using GAE repository.");
        }

        CACHE = CacheFactory.getCache(
                TagGAERepository.class.getSimpleName() + "Cache");
    }

    @Override
    public String getName() {
        return Tag.TAG;
    }

    @Override
    public List<JSONObject> getArticles(final String tagTitle,
                                        final int currentPageNum,
                                        final int pageSize)
            throws RepositoryException {
        final List<JSONObject> ret = new ArrayList<JSONObject>();

        try {
            final JSONObject tag = getByTitle(tagTitle);
            final String tagId = tag.getString(Keys.OBJECT_ID);
            final JSONObject result =
                    tagArticleRepository.getByTagId(tagId,
                                                    currentPageNum,
                                                    pageSize);
            final JSONArray tagArticleRelations =
                    result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < tagArticleRelations.length(); i++) {
                final JSONObject tagArticleRelation =
                        tagArticleRelations.getJSONObject(i);
                final String articleId =
                        tagArticleRelation.getString(Article.ARTICLE + "_"
                                                     + Keys.OBJECT_ID);
                final JSONObject article = articleRepository.get(articleId);

                ret.add(article);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return ret;
    }

    @Override
    public List<JSONObject> getRecentArticles(final String tagTitle,
                                              final int fetchSize)
            throws RepositoryException {
       return getArticles(tagTitle, 1, fetchSize);
    }

    @Override
    public JSONObject getByTitle(final String tagTitle)
            throws RepositoryException {
        final String cacheKey = "getByTitle[" + tagTitle + "]";
        JSONObject ret = (JSONObject) CACHE.get(cacheKey);

        if (null == ret) {
            final Query query = new Query(Tag.TAG);
            query.addFilter(Tag.TAG_TITLE_LOWER_CASE,
                            Query.FilterOperator.EQUAL, tagTitle.toLowerCase());
            final PreparedQuery preparedQuery = getDatastoreService().prepare(
                    query);
            final Entity entity = preparedQuery.asSingleEntity();
            if (null == entity) {
                return null;
            }

            final Map<String, Object> properties = entity.getProperties();

            ret = new JSONObject(properties);
            CACHE.put(cacheKey, ret);
        }

        return ret;
    }

    @Override
    public List<JSONObject> getMostUsedTags(final int num) {
        final String cacheKey = "getMostUsedTags[" + num + "]";
        @SuppressWarnings("unchecked")
        List<JSONObject> ret =
                (List<JSONObject>) CACHE.get(cacheKey);
        if (null != ret) {
            LOGGER.log(Level.FINEST, "Got the most used tags from cache");
        } else {
            final Query query = new Query(getName());
            query.addSort(Tag.TAG_REFERENCE_COUNT,
                          Query.SortDirection.DESCENDING);
            final PreparedQuery preparedQuery = getDatastoreService().prepare(
                    query);
            final QueryResultIterable<Entity> queryResultIterable =
                    preparedQuery.asQueryResultIterable(FetchOptions.Builder.
                    withLimit(num));

            ret = new ArrayList<JSONObject>();
            for (final Entity entity : queryResultIterable) {
                final JSONObject tag = entity2JSONObject(entity);
                ret.add(tag);
            }
            CACHE.put(cacheKey, ret);

            LOGGER.log(Level.FINEST,
                       "Got the most used tags, then put it into cache");
        }

        return ret;
    }

    /**
     * Gets the {@link UserGAERepository} singleton.
     *
     * @return the singleton
     */
    public static TagGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private TagGAERepository() {
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
        private static final TagGAERepository SINGLETON =
                new TagGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
