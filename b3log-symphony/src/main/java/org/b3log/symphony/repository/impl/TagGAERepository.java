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
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Common;
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
 * @version 1.0.0.5, Nov 11, 2011
 */
public final class TagGAERepository extends AbstractRepository
        implements TagRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagGAERepository.class.getName());
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
                if (article.getBoolean(Common.STATE)) {
                    ret.add(article);
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return ret;
    }

    @Override
    public List<JSONObject> getTopArticles(final String tagTitle,
                                           final int fetchSize)
            throws RepositoryException {
        final List<JSONObject> ret = new ArrayList<JSONObject>();
        try {
            final JSONObject tag = getByTitle(tagTitle);
            final String tagId = tag.getString(Keys.OBJECT_ID);
            final JSONObject result =
                    tagArticleRepository.getTopByTagId(tagId,
                                                       1,
                                                       fetchSize);
            final JSONArray tagArticleRelations =
                    result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < tagArticleRelations.length(); i++) {
                final JSONObject tagArticleRelation =
                        tagArticleRelations.getJSONObject(i);
                final String articleId =
                        tagArticleRelation.getString(Article.ARTICLE + "_"
                                                     + Keys.OBJECT_ID);
                final JSONObject article = articleRepository.get(articleId);
                if (article.getBoolean(Common.STATE)) {
                    ret.add(article);
                }
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
        final Query query = new Query().setPageCount(1);
        query.addFilter(Tag.TAG_TITLE_LOWER_CASE, FilterOperator.EQUAL,
                        tagTitle.toLowerCase());
        try {
            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            if (0 == array.length()) {
                return null;
            }

            return array.getJSONObject(0);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return null;
        }
    }

    @Override
    public List<JSONObject> getMostUsedTags(final int num) {
        final Query query = new Query().addSort(Tag.TAG_REFERENCE_COUNT,
                                                SortDirection.DESCENDING).
                setCurrentPageNum(1).
                setPageSize(num).
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

    /**
     * Gets the {@link UserGAERepository} singleton.
     *
     * @return the singleton
     */
    public static TagGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private TagGAERepository(final String name) {
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
        private static final TagGAERepository SINGLETON =
                new TagGAERepository(Tag.TAG);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
