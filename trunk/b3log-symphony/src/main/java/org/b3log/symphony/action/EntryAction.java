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

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import org.b3log.latke.action.ActionException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.MD5;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Comment;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.ArticleCommentRepository;
import org.b3log.symphony.repository.CommentRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.ArticleCommentGAERepository;
import org.b3log.symphony.repository.impl.CommentGAERepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Entry action. entry.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Feb 9, 2011
 */
public final class EntryAction extends AbstractCacheablePageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(EntryAction.class.getName());
    /**
     * Article-Comment repository.
     */
    private ArticleCommentRepository articleCommentRepository =
            ArticleCommentGAERepository.getInstance();
    /**
     * Comment repository.
     */
    private CommentRepository commentRepository =
            CommentGAERepository.getInstance();
    /**
     * Language service.
     */
    private static final LangPropsService LANG_PROP_SVC =
            LangPropsService.getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();
    /**
     * Languages.
     */
    private static final Map<String, String> LANGS;
    /**
     * Default sign.
     */
    private static final String DEFAULT_SIGN;
    /**
     * URL fetch service.
     */
    private static URLFetchService urlFetchService =
            URLFetchServiceFactory.getURLFetchService();
    /**
     * Default user thumbnail.
     */
    private static final String DEFAULT_USER_THUMBNAIL =
            "default-user-thumbnail.png";

    static {
        try {
            LANGS = LANG_PROP_SVC.getAll(
                    Latkes.getDefaultLocale());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        DEFAULT_SIGN = LANGS.get("defaultSign");
    }

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            final JSONObject article = (JSONObject) request.getAttribute(
                    Article.ARTICLE);
            if (null == article) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            }

            final List<JSONObject> comments = new ArrayList<JSONObject>();

            final String articleId = article.getString(Keys.OBJECT_ID);
            final JSONObject queryStringJSONObject =
                    getQueryStringJSONObject(request);
            final int currentPageNum = queryStringJSONObject.optInt("p", 1);
            final int fetchSize = 4;
            final int windowSize = 10;
            final JSONObject result =
                    articleCommentRepository.getByArticleId(articleId,
                                                            currentPageNum,
                                                            fetchSize);

            final int pageCount = result.getJSONObject(
                    Pagination.PAGINATION).getInt(
                    Pagination.PAGINATION_PAGE_COUNT);
            final JSONArray articleCmtRelations =
                    result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < articleCmtRelations.length(); i++) {
                final JSONObject articleCmtRelation =
                        articleCmtRelations.getJSONObject(i);
                final String cmtId = articleCmtRelation.getString(
                        Comment.COMMENT + "_" + Keys.OBJECT_ID);
                final JSONObject cmt = commentRepository.get(cmtId);
                if (!cmt.getBoolean(Common.STATE)) { // This comment is forbidden
                    cmt.put(Comment.COMMENT_CONTENT, "自由、开放的环境需要大家共同维护 :-)");
                    cmt.put(Common.SIGN, DEFAULT_SIGN);
                    cmt.put(Comment.COMMENTER_URL_REF, "http://www.b3log.org");
                } else {
                    cmt.put(Comment.COMMENT_CONTENT,
                            cmt.getString(Comment.COMMENT_CONTENT).replaceAll(
                            AddArticleCommentAction.ENTER_ESC, "<br/>"));
                    final String commenterEmail = cmt.getString(
                            Comment.COMMENTER_EMAIL);
                    final JSONObject user = userRepository.getByEmail(
                            commenterEmail);
                    cmt.put(Comment.COMMENTER_URL_REF,
                            user.getString(User.USER_URL));
                    cmt.put(Common.SIGN, user.getString(Common.SIGN));
                }

                comments.add(cmt);
            }

            article.put(Article.ARTICLE_COMMENTS_REF, (Object) comments);
            final String authorId = article.getString(Common.AUTHOR_ID);
            final JSONObject author = userRepository.get(authorId);
            final String name = author.getString(User.USER_NAME);
            article.put(Article.ARTICLE_AUTHOR_NAME_REF, name);
            final String url = author.getString(User.USER_URL);
            article.put(Article.ARTICLE_AUTHOR_URL_REF, url);
            final String thumbnailURL = getThumbnailURL(
                    author.getString(User.USER_EMAIL));
            article.put(Article.ARTICLE_AUTHOR_THUMBNAIL_URL_REF,
                        thumbnailURL);
            final String sign = author.getString(Common.SIGN);
            article.put(Common.SIGN, sign);

            ret.put(Article.ARTICLE, article);

            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, fetchSize, pageCount,
                                       windowSize);
            ret.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            ret.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            ret.putAll(LANGS);
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

    /**
     * Gets thumbnail URL from <a href="http://www.gravatar.com">
     * Gravatar</a> for the specified email.
     *
     * @param email the specified email
     * @return the thumbnail URL, if not found from <a href="http://www.gravatar.com">
     * Gravatar</a>, using the default URL {@value #DEFAULT_USER_THUMBNAIL}
     * instead.
     * @throws Exception exception
     */
    private static String getThumbnailURL(final String email)
            throws Exception {
        String ret = null;

        // Try to set thumbnail URL using Gravatar service
        final String hashedEmail = MD5.hash(email.toLowerCase());
        final int size = 60;
        final URL gravatarURL =
                new URL("http://www.gravatar.com/avatar/" + hashedEmail + "?s="
                        + size + "&r=G");
        try {
            final HTTPResponse response = urlFetchService.fetch(gravatarURL);
            final int statusCode = response.getResponseCode();

            if (HttpServletResponse.SC_OK == statusCode) {
                final List<HTTPHeader> headers = response.getHeaders();
                boolean defaultFileLengthMatched = false;
                for (final HTTPHeader httpHeader : headers) {
                    if ("Content-Length".equalsIgnoreCase(httpHeader.getName())) {
                        if (httpHeader.getValue().equals("2147")) {
                            defaultFileLengthMatched = true;
                        }
                    }
                }

                if (!defaultFileLengthMatched) {
                    ret = "http://www.gravatar.com/avatar/"
                          + hashedEmail + "?s=" + size + "&r=G";
                    LOGGER.log(Level.FINEST, "Thumbnail[URL={0}]", ret);

                    return ret;
                }
            } else {
                LOGGER.log(Level.WARNING,
                           "Can not fetch thumbnail from Gravatar[commentEmail={0}, statusCode={1}]",
                           new Object[]{email, statusCode});
            }
        } catch (final IOException e) {
            LOGGER.warning(e.getMessage());
            LOGGER.log(Level.WARNING,
                       "Can not fetch thumbnail from Gravatar[commentEmail={0}]",
                       email);
        }

        if (null == ret) {
            LOGGER.log(Level.WARNING,
                       "Not supported yet for comment thumbnail for email[{0}]",
                       email);
            ret = "/images/" + DEFAULT_USER_THUMBNAIL;
        }

        return ret;
    }
}
