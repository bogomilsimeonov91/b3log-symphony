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

package org.b3log.symphony.action;

import org.b3log.latke.action.ActionException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.util.Strings;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.TagArticleRepository;
import org.b3log.symphony.repository.TagRepository;
import org.b3log.symphony.repository.TagUserRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.ArticleGAERepository;
import org.b3log.symphony.repository.impl.TagArticleGAERepository;
import org.b3log.symphony.repository.impl.TagGAERepository;
import org.b3log.symphony.repository.impl.TagUserGAERepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.b3log.symphony.util.Langs;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Tag entries action. tag-entries.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Feb 9, 2011
 */
public final class TagEntriesAction extends AbstractCacheablePageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagEntriesAction.class.getName());
    /**
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository =
            TagArticleGAERepository.getInstance();
    /**
     * Tag-User repository.
     */
    private TagUserRepository tagUserRepository =
            TagUserGAERepository.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagGAERepository.getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            String tagTitle = request.getRequestURI().substring(
                    "/tags/".length());
            tagTitle = URLDecoder.decode(tagTitle, "UTF-8");
            LOGGER.log(Level.FINER, "Tag[title={0}]", tagTitle);
            final JSONObject tag = tagRepository.getByTitle(tagTitle);
            if (null == tag) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            }

            final JSONObject queryStringJSONObject =
                    getQueryStringJSONObject(request);
            ret.put(Tag.TAG, tag);

            final List<JSONObject> topAuthors = new ArrayList<JSONObject>();
            final List<String> topAuthorIds = tagUserRepository.getTopTagUsers(
                    tag.getString(Keys.OBJECT_ID), 5);
            for (final String topAuthorId : topAuthorIds) {
                final JSONObject user = userRepository.get(topAuthorId);

                final JSONObject topAuthor = new JSONObject();
                topAuthor.put(Keys.OBJECT_ID, topAuthorId);
                final String topAuthorName = user.getString(User.USER_NAME);
                topAuthor.put(User.USER_NAME, topAuthorName);
                final String topAuthorURL = user.getString(User.USER_URL);
                topAuthor.put(User.USER_URL, topAuthorURL);
                topAuthors.add(topAuthor);
                final String thumbnailFileId =
                        user.optString(Common.USER_THUMBNAIL_FILE_ID);
                if (Strings.isEmptyOrNull(thumbnailFileId)) {
                    topAuthor.put(Common.USER_THUMBNAIL_URL,
                                  EntryAction.DEFAULT_USER_THUMBNAIL_URL);
                } else {
                    topAuthor.put(Common.USER_THUMBNAIL_URL,
                                  "/file?oId=" + thumbnailFileId);
                }
            }

            ret.put(Common.TAG_TOP_USERS, topAuthors);

            final int currentPageNum = queryStringJSONObject.optInt("p", 1);

            final int fetchSize = 20;
            final List<JSONObject> articles = new ArrayList<JSONObject>();
            final String tagId = tag.getString(Keys.OBJECT_ID);
            final JSONObject result =
                    tagArticleRepository.getByTagId(tagId,
                                                    currentPageNum,
                                                    fetchSize);
            final int pageCount = result.getJSONObject(
                    Pagination.PAGINATION).getInt(
                    Pagination.PAGINATION_PAGE_COUNT);
            final int windowSize = 10;
            final JSONArray tagArticleRelations =
                    result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < tagArticleRelations.length(); i++) {
                final JSONObject tagArticleRelation =
                        tagArticleRelations.getJSONObject(i);
                final String articleId =
                        tagArticleRelation.getString(Article.ARTICLE + "_"
                                                     + Keys.OBJECT_ID);
                final JSONObject article = articleRepository.get(articleId);
                if (!article.getBoolean(Common.STATE)) {
                    continue;
                }

                final String authorId = article.getString(
                        Common.AUTHOR_ID);
                final JSONObject author = userRepository.get(authorId);
                final String name = author.getString(User.USER_NAME);
                article.put(Article.ARTICLE_AUTHOR_NAME_REF, name);
                final String url = author.getString(User.USER_URL);
                article.put(Article.ARTICLE_AUTHOR_URL_REF, url);
                final String sign = author.getString(Common.SIGN);
                article.put(Common.SIGN, sign);
                final String thumbnailFileId =
                        author.optString(Common.USER_THUMBNAIL_FILE_ID);
                if (Strings.isEmptyOrNull(thumbnailFileId)) {
                    article.put(Article.ARTICLE_AUTHOR_THUMBNAIL_URL_REF,
                                EntryAction.DEFAULT_USER_THUMBNAIL_URL);
                } else {
                    article.put(Article.ARTICLE_AUTHOR_THUMBNAIL_URL_REF,
                                "/file?oId=" + thumbnailFileId);
                }

                articles.add(article);
            }

            ret.put(Article.ARTICLES, (Object) articles);

            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, fetchSize, pageCount,
                                       windowSize);
            ret.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            ret.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            ret.putAll(Langs.all());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }

        return ret;
    }

    @Override
    protected String getPageName(final String requestURI) {
        return "tag-entries.ftl";
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
