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

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.b3log.latke.action.ActionException;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.ArticleGAERepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.b3log.symphony.util.Langs;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * User action. user.ftl
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Feb 11, 2011
 */
public final class UserAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(UserAction.class.getName());
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository = ArticleGAERepository.
            getInstance();

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        ret.putAll(Langs.all());

        final String userName = request.getRequestURI().substring(
                "/users/".length());
        try {
            final JSONObject user = userRepository.getByName(userName);
            if (null == user) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            }

            final JSONObject queryStringJSONObject =
                    getQueryStringJSONObject(request);
            final int currentPageNum = queryStringJSONObject.optInt("p", 1);
            final int fetchSize = 5;

            final String userId = user.getString(Keys.OBJECT_ID);
            final Query query = new Query();
            query.setCurrentPageNum(currentPageNum).setPageSize(fetchSize).
                    addFilter(Common.AUTHOR_ID, FilterOperator.EQUAL, userId).
                    addSort(Article.ARTICLE_CREATE_DATE,
                            SortDirection.DESCENDING);
            final JSONObject result = articleRepository.get(query);
            final JSONArray articles = result.getJSONArray(Keys.RESULTS);
            ret.put(Article.ARTICLES, CollectionUtils.jsonArrayToList(articles));

            final int pageCount = result.getJSONObject(
                    Pagination.PAGINATION).getInt(
                    Pagination.PAGINATION_PAGE_COUNT);
            final int windowSize = 10;
            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, fetchSize, pageCount,
                                       windowSize);
            ret.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            ret.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return ret;
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject requestJSONObject,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getPageName(final String requestURI) {
        return "user.ftl";
    }
}
