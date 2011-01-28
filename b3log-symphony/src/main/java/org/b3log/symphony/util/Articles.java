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

package org.b3log.symphony.util;

import java.util.ArrayList;
import java.util.List;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.repository.TagArticleRepository;
import org.b3log.symphony.repository.TagRepository;
import org.b3log.symphony.repository.impl.TagArticleGAERepository;
import org.b3log.symphony.repository.impl.TagGAERepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 28, 2011
 */
public final class Articles {

    /**
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository =
            TagArticleGAERepository.getInstance();
    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagGAERepository.getInstance();

    /**
     * Adds relation of the specified tags and article.
     *
     * @param tags the specified tags
     * @param article the specified article
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void addTagArticleRelation(final JSONArray tags,
                                      final JSONObject article)
            throws JSONException, RepositoryException {
        for (int i = 0; i < tags.length(); i++) {
            final JSONObject tag = tags.getJSONObject(i);
            final JSONObject tagArticleRelation = new JSONObject();

            tagArticleRelation.put(Tag.TAG + "_" + Keys.OBJECT_ID,
                                   tag.getString(Keys.OBJECT_ID));
            tagArticleRelation.put(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                                   article.getString(Keys.OBJECT_ID));

            tagArticleRepository.add(tagArticleRelation);
        }
    }

    /**
     * Adds tags for every article of the specified articles.
     *
     * @param articles the specified articles
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    public void addTags(final List<JSONObject> articles)
            throws RepositoryException, JSONException {
        for (final JSONObject article : articles) {
            final String articleId = article.getString(Keys.OBJECT_ID);
            final List<JSONObject> tagArticleRelations =
                    tagArticleRepository.getByArticleId(articleId);

            final List<JSONObject> tags = new ArrayList<JSONObject>();
            for (int i = 0; i < tagArticleRelations.size(); i++) {
                final JSONObject tagArticleRelation =
                        tagArticleRelations.get(i);
                final String tagId =
                        tagArticleRelation.getString(Tag.TAG + "_"
                                                     + Keys.OBJECT_ID);
                final JSONObject tag = tagRepository.get(tagId);
                tags.add(tag);
            }

            article.put(Article.ARTICLE_TAGS,
                        /* Avoid convert to JSONArray, which FreeMarker can't
                         * process in <#list/> */
                        (Object) tags);
        }
    }

    /**
     * Gets the {@link ArticleUtils} singleton.
     *
     * @return the singleton
     */
    public static Articles getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Articles() {
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
        private static final Articles SINGLETON = new Articles();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
