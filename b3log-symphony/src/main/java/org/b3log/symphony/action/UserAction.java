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
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.symphony.action.util.Filler;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Comment;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.CommentRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.ArticleGAERepository;
import org.b3log.symphony.repository.impl.CommentGAERepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.b3log.symphony.util.Langs;
import org.b3log.symphony.util.Symphonys;
import org.b3log.symphony.util.Users;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * User action. user.ftl
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Aug 6, 2011
 */
public final class UserAction extends AbstractCacheablePageAction {

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
    /**
     * Comment repository.
     */
    private CommentRepository commentRepository = CommentGAERepository.
            getInstance();
    /**
     * Entry fetch size.
     */
    public static final int ENTRY_FETCH_SIZE = Integer.valueOf(
            Symphonys.get("userEntriesCntPerPage"));
    /**
     * Comment fetch size.
     */
    public static final int CMT_FETCH_SIZE = Integer.valueOf(
            Symphonys.get("userCmtsCntPerPage"));

    @Override
    protected Map<?, ?> doFreeMarkerAction(
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

            ret.put(User.USER_URL, user.getString(User.USER_URL));
            final String sign = Users.getUserSignHTML(user);
            ret.put(Common.SIGN, sign);
            ret.put(User.USER_NAME, userName);
            ret.put(Common.USER_THUMBNAIL_URL,
                    user.getString(Common.USER_THUMBNAIL_URL));

            fillEntries(user, ret);
            fillComments(user, ret);

            Filler.fillCommon(ret);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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

    /**
     * Fills the specified data model with entries.
     *
     * @param user the specified user
     * @param dataModel the specified data model
     * @throws Exception exception
     */
    private void fillEntries(final JSONObject user,
                             final Map<String, Object> dataModel)
            throws Exception {
        final String userId = user.getString(Keys.OBJECT_ID);
        final Query query = new Query();
        query.setCurrentPageNum(1).setPageSize(ENTRY_FETCH_SIZE).
                addFilter(Common.AUTHOR_ID,
                          FilterOperator.EQUAL, userId).
                addSort(Article.ARTICLE_CREATE_DATE,
                        SortDirection.DESCENDING);
        // TODO: add entry count into user repository
        final JSONObject result = articleRepository.get(query);
        final JSONArray articles =
                result.getJSONArray(Keys.RESULTS);
        LOGGER.log(Level.FINER, "User recent entries[size={0}]",
                   articles.length());
        dataModel.put(Article.ARTICLES,
                      CollectionUtils.jsonArrayToList(articles));
        final int pageCount =
                result.getJSONObject(Pagination.PAGINATION).
                getInt(Pagination.PAGINATION_PAGE_COUNT);
        final int windowSize = 10;
        final List<Integer> pageNums =
                Paginator.paginate(1, ENTRY_FETCH_SIZE, pageCount,
                                   windowSize);
        dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
        dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
    }

    /**
     * Fills the specified data model with comments.
     *
     * @param user the specified user
     * @param dataModel the specified data model
     * @throws Exception exception
     */
    private void fillComments(final JSONObject user,
                              final Map<String, Object> dataModel)
            throws Exception {
        final String userId = user.getString(Keys.OBJECT_ID);
        final Query query = new Query();
        query.setCurrentPageNum(1).setPageSize(CMT_FETCH_SIZE).
                addFilter(Comment.COMMENTER_ID, FilterOperator.EQUAL, userId).
                addSort(Comment.COMMENT_DATE, SortDirection.DESCENDING);
        // TODO: add comment count into user repository
        final JSONObject result = commentRepository.get(query);
        final JSONArray commentArray = result.getJSONArray(Keys.RESULTS);

        dataModel.put(Comment.COMMENTS, CollectionUtils.jsonArrayToList(
                commentArray));
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject requestJSONObject,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getTemplateName(final String requestURI) {
        return "user.ftl";
    }
}
