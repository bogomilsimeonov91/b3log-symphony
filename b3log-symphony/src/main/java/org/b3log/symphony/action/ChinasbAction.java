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

import java.io.IOException;
import java.util.ArrayList;
import org.b3log.latke.action.ActionException;
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
import org.b3log.symphony.action.util.Filler;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.TagArticleRepository;
import org.b3log.symphony.repository.TagRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.ArticleGAERepository;
import org.b3log.symphony.repository.impl.TagArticleGAERepository;
import org.b3log.symphony.repository.impl.TagGAERepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.b3log.symphony.util.Langs;
import org.b3log.symphony.util.Users;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Chinasb action. chinasb.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Aug 6, 2011
 */
public final class ChinasbAction extends AbstractCacheablePageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ChinasbAction.class.getName());
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
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository =
            TagArticleGAERepository.getInstance();
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
        ret.putAll(Langs.all());
        Filler.fillCommon(ret);

        final List<JSONObject> articles = new ArrayList<JSONObject>();
        ret.put(Article.ARTICLES, articles);
        ret.put(Pagination.PAGINATION_PAGE_COUNT, 0);
        ret.put(Pagination.PAGINATION_PAGE_NUMS, new ArrayList<Object>());

        try {
            String p = request.getParameter("p");
            if (Strings.isEmptyOrNull(p)) {
                p = "1";
            }

            final int currentPageNum = Integer.parseInt(p);

            final JSONObject chinasbTag = tagRepository.getByTitle("chinasb");
            if (null != chinasbTag) {
                final String tagId = chinasbTag.getString(Keys.OBJECT_ID);
                final JSONObject result =
                        tagArticleRepository.getByTagId(tagId, currentPageNum,
                                                        UserAction.ENTRY_FETCH_SIZE);
                final JSONArray tagArticleRelations =
                        result.getJSONArray(Keys.RESULTS);


                LOGGER.log(Level.FINE, "Getting articles....");
                for (int i = 0; i < tagArticleRelations.length(); i++) {
                    final JSONObject tagArticleRel =
                            tagArticleRelations.getJSONObject(i);
                    final JSONObject article =
                            articleRepository.get(tagArticleRel.getString(
                            Article.ARTICLE + "_" + Keys.OBJECT_ID));
                    articles.add(article);

                    final String authorId = article.getString(
                            Common.AUTHOR_ID);
                    final JSONObject author = userRepository.get(authorId);
                    final String name = author.getString(User.USER_NAME);
                    article.put(Article.ARTICLE_AUTHOR_NAME_REF, name);
                    final String url = author.getString(User.USER_URL);
                    article.put(Article.ARTICLE_AUTHOR_URL_REF, url);
                    final String sign = Users.getUserSignHTML(author);
                    article.put(Common.SIGN, sign);
                    article.put(Article.ARTICLE_AUTHOR_THUMBNAIL_URL_REF,
                                author.getString(Common.USER_THUMBNAIL_URL));
                }
                LOGGER.log(Level.FINE, "Got articles....");

                final int pageCount =
                        result.getJSONObject(Pagination.PAGINATION).
                        getInt(Pagination.PAGINATION_PAGE_COUNT);
                final int windowSize = 20;
                final List<Integer> pageNums =
                        Paginator.paginate(currentPageNum,
                                           UserAction.ENTRY_FETCH_SIZE,
                                           pageCount,
                                           windowSize);
                ret.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
                ret.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }

        request.setAttribute(AbstractCacheablePageAction.CACHED_LINK,
                             "Unspecified");
        request.setAttribute(AbstractCacheablePageAction.CACHED_OID,
                             "Unspecified");
        request.setAttribute(AbstractCacheablePageAction.CACHED_TITLE,
                             "Unspecified");
        request.setAttribute(AbstractCacheablePageAction.CACHED_TYPE,
                             "Unspecified");

        return ret;
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
