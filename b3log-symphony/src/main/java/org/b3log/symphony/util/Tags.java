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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.TagArticleRepository;
import org.b3log.symphony.repository.TagRepository;
import org.b3log.symphony.repository.impl.ArticleGAERepository;
import org.b3log.symphony.repository.impl.TagArticleGAERepository;
import org.b3log.symphony.repository.impl.TagGAERepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tag utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 28, 2011
 */
public final class Tags {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Tags.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
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
     * Tags the specified article with the specified tag titles.
     *
     * @param tagTitles the specified tag titles
     * @param article the specified article
     * @return an array of tags
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    public JSONArray tag(final String[] tagTitles,
                         final JSONObject article)
            throws RepositoryException, JSONException {
        final JSONArray ret = new JSONArray();
        for (int i = 0; i < tagTitles.length; i++) {
            final String tagTitle = tagTitles[i].trim();
            JSONObject tag = tagRepository.getByTitle(tagTitle);
            String tagId = null;
            if (null == tag) {
                LOGGER.log(Level.FINEST,
                           "Found a new tag[title={0}] in article[title={1}]",
                           new Object[]{
                            tagTitle, article.getString(Article.ARTICLE_TITLE)});
                tag = new JSONObject();
                tag.put(Tag.TAG_TITLE, tagTitle);
                tag.put(Tag.TAG_COMMENT_COUNT, 0);
                tag.put(Tag.TAG_REFERENCE_COUNT, 1);
                tag.put(Tag.TAG_TITLE_LOWER_CASE, tagTitle.toLowerCase());

                tagId = tagRepository.add(tag);
                tag.put(Keys.OBJECT_ID, tagId);
            } else {
                tagId = tag.getString(Keys.OBJECT_ID);
                LOGGER.log(Level.FINEST,
                           "Found a existing tag[title={0}, oId={1}] in article[title={2}]",
                           new Object[]{tag.getString(Tag.TAG_TITLE),
                                        tag.getString(Keys.OBJECT_ID),
                                        article.getString(Article.ARTICLE_TITLE)});
                final JSONObject tagToUpdate =
                        new JSONObject(tag, JSONObject.getNames(tag));
                final int refCnt = tag.getInt(Tag.TAG_REFERENCE_COUNT);
                tagToUpdate.put(Tag.TAG_REFERENCE_COUNT, refCnt + 1);

                tagRepository.update(tagId, tagToUpdate);
            }

            ret.put(tag);
        }

        return ret;
    }

    /**
     * Decrements reference count of every tag of an article specified by the
     * given article id.
     *
     * @param articleId the given article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decTagRefCount(final String articleId)
            throws JSONException, RepositoryException {
        final List<JSONObject> tags = tagArticleRepository.getByArticleId(
                articleId);
        final JSONObject article = articleRepository.get(articleId);

        for (final JSONObject tag : tags) {
            final String tagId = tag.getString(Keys.OBJECT_ID);
            final int refCnt = tag.getInt(Tag.TAG_REFERENCE_COUNT);
            tag.put(Tag.TAG_REFERENCE_COUNT, refCnt - 1);

            tagRepository.update(tagId, tag);
            LOGGER.log(Level.FINEST,
                       "Deced tag[title={0}, refCnt={1}] of article[oId={2}]",
                       new Object[]{tag.getString(Tag.TAG_TITLE),
                                    tag.getInt(Tag.TAG_REFERENCE_COUNT),
                                    articleId});
        }

        LOGGER.log(Level.FINER,
                   "Deced all tag reference count of article[oId={0}]",
                   articleId);
    }

    /**
     * Gets the {@link Tags} singleton.
     *
     * @return the singleton
     */
    public static Tags getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Tags() {
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
        private static final Tags SINGLETON = new Tags();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
