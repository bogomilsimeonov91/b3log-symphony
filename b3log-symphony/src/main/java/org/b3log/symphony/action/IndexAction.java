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
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Strings;
import org.b3log.symphony.action.util.Filler;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.ArticleGAERepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.b3log.symphony.util.Langs;
import org.b3log.symphony.util.Symphonys;
import org.b3log.symphony.util.Users;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Index action. index.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Aug 6, 2011
 */
public final class IndexAction extends AbstractCacheablePageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(IndexAction.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository = ArticleGAERepository.
            getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();
    /**
     * Recent articles count.
     */
    public static final int RECENT_ARTICLES_CNT =
            Integer.valueOf(Symphonys.get("recentEntriesCntPerPage"));

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            ret.putAll(Langs.all());

            String p = request.getParameter("p");
            if (Strings.isEmptyOrNull(p)) {
                p = "1";
            }

            final int currentPageNum = Integer.parseInt(p);

            final Query query = new Query();
            query.setCurrentPageNum(currentPageNum).
                    setPageSize(RECENT_ARTICLES_CNT).
                    addSort(Article.ARTICLE_CREATE_DATE,
                            SortDirection.DESCENDING);
            LOGGER.log(Level.FINE, "Getting articles....");
            final JSONObject result = articleRepository.get(query);

            final JSONArray articles = result.getJSONArray(Keys.RESULTS);
            for (int i = 0; i < articles.length(); i++) {
                final JSONObject article = articles.getJSONObject(i);
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

            ret.put(Article.ARTICLES, CollectionUtils.jsonArrayToList(articles));
            final int pageCount =
                    result.getJSONObject(Pagination.PAGINATION).
                    getInt(Pagination.PAGINATION_PAGE_COUNT);
            final int windowSize = 20;
            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum,
                                       UserAction.ENTRY_FETCH_SIZE, pageCount,
                                       windowSize);
            ret.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            ret.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            Filler.fillCommon(ret);

            request.setAttribute(AbstractCacheablePageAction.CACHED_LINK,
                                 "Unspecified");
            request.setAttribute(AbstractCacheablePageAction.CACHED_OID,
                                 "Unspecified");
            request.setAttribute(AbstractCacheablePageAction.CACHED_TITLE,
                                 "Unspecified");
            request.setAttribute(AbstractCacheablePageAction.CACHED_TYPE,
                                 "Unspecified");
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
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
