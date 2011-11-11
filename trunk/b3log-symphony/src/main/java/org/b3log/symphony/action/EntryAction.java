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
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.util.Strings;
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
 * Entry action. entry.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, Nov 11, 2011
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
     * Comment repository.
     */
    private CommentRepository commentRepository =
            CommentGAERepository.getInstance();
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
     * Default sign.
     */
    public static final String DEFAULT_SIGN = Langs.get("defaultSign");
    /**
     * Default user thumbnail URL.
     */
    public static final String DEFAULT_USER_THUMBNAIL_URL =
            "/images/default-user-thumbnail.png";
    /**
     * Begin comment reference(reply) HTML.
     */
    public static final String BEGIN_CMT_REF_HTML = "<div class='ref'>";
    /**
     * End comment reference(reply) HTML.
     */
    public static final String END_CMT_REF_HTML = "</div>";
    /**
     * Entry comments per page.
     */
    public static final int ENTRY_CMTS_PER_PAGE =
            Integer.valueOf(Symphonys.get("entryCmtsCntPerPage"));

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();
        final String entryId = request.getRequestURI().substring(
                "/entries/".length());
        try {
            final JSONObject article = articleRepository.get(entryId);
            if (null == article) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            }

            final List<JSONObject> comments = new ArrayList<JSONObject>();

            final String articleId = article.getString(Keys.OBJECT_ID);
            String p = request.getParameter("p");
            if (Strings.isEmptyOrNull(p)) {
                p = "1";
            }

            final int currentPageNum = Integer.parseInt(p);
            final int windowSize = 10;

            final Query query = new Query();
            query.setCurrentPageNum(currentPageNum).
                    setPageSize(ENTRY_CMTS_PER_PAGE).
                    addFilter(Comment.COMMENT_ENTRY_ID,
                              FilterOperator.EQUAL,
                              articleId).
                    setPageCount(1);
            final JSONObject result = commentRepository.get(query);

            final int commentCount =
                    article.getInt(Article.ARTICLE_COMMENT_COUNT);
            final int pageCount =
                    (int) Math.ceil((double) commentCount
                                    / (double) ENTRY_CMTS_PER_PAGE);

            final JSONArray articleCmts =
                    result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < articleCmts.length(); i++) {
                final JSONObject cmt = articleCmts.getJSONObject(i);

                if (!cmt.getBoolean(Common.STATE)) { // This comment is forbidden
                    cmt.put(Comment.COMMENT_CONTENT,
                            Langs.get("defaultCmtContent"));
                    cmt.put(Common.SIGN, DEFAULT_SIGN);
                    cmt.put(Comment.COMMENTER_URL_REF, Symphonys.get("host"));
                } else {
                    cmt.put(Comment.COMMENT_CONTENT,
                            cmt.getString(Comment.COMMENT_CONTENT));
                    final String commenterId = cmt.getString(
                            Comment.COMMENTER_ID);
                    final JSONObject user = userRepository.get(commenterId);
                    cmt.put(Comment.COMMENTER_URL_REF,
                            user.getString(User.USER_URL));
                    cmt.put(Comment.COMMENTER_NAME_REF,
                            user.getString(User.USER_NAME));
                    final String sign = Users.getUserSignHTML(user);
                    cmt.put(Common.SIGN, sign);
                    cmt.put(Comment.COMMENT_THUMBNAIL_URL_REF,
                            user.getString(Common.USER_THUMBNAIL_URL));
                }

                final String originalCmtId =
                        cmt.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID);
                final StringBuilder repliesBuilder = new StringBuilder();
                composeReplies(originalCmtId, repliesBuilder);
                final String cmtContent =
                        cmt.getString(Comment.COMMENT_CONTENT);
                repliesBuilder.append(cmtContent);
                cmt.put(Comment.COMMENT_CONTENT, repliesBuilder.toString());

                comments.add(cmt);
            }

            article.put(Article.ARTICLE_COMMENTS_REF, (Object) comments);
            final String authorId = article.getString(Common.AUTHOR_ID);
            final JSONObject author = userRepository.get(authorId);
            final String name = author.getString(User.USER_NAME);
            article.put(Article.ARTICLE_AUTHOR_NAME_REF, name);
            final String url = author.getString(User.USER_URL);
            article.put(Article.ARTICLE_AUTHOR_URL_REF, url);
            article.put(Article.ARTICLE_AUTHOR_THUMBNAIL_URL_REF,
                        author.getString(Common.USER_THUMBNAIL_URL));
            final String sign = Users.getUserSignHTML(author);
            article.put(Common.SIGN, sign);

            ret.put(Article.ARTICLE, article);

            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, ENTRY_CMTS_PER_PAGE,
                                       pageCount,
                                       windowSize);
            ret.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            ret.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            ret.putAll(Langs.all());
            Filler.fillCommon(ret);
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
    protected String getTemplateName(final String requestURI) {
        return "entry.ftl";
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Composes comment and its replies withe the specified original comment id
     * and replies composite builder.
     *
     * <p>
     * The specified replies composite string as the following finally:
     * <pre>
     * &lt;div class='ref'&gt;
     *     cmt block(commter info and comment content, etc)
     *     &lt;div class='ref'&gt;
     *         cmt ref block
     *         &lt;div class='ref'&gt;
     *             cmt ref ref block
     *         &lt;/div&gt;
     *     &lt;/div&gt;
     * &lt;/div&gt;
     * </pre>
     * </p>
     *
     * @param originalCommentId the specified original comment id
     * @param repliesBuilder the specified replies composite builder
     * @throws Exception exception
     */
    // TODO: use local cache instead of GAE memcache used in Repository
    private void composeReplies(final String originalCommentId,
                                final StringBuilder repliesBuilder)
            throws Exception {
        if (Strings.isEmptyOrNull(originalCommentId)) {
            return;
        }

        final JSONObject originalComment =
                commentRepository.get(originalCommentId);
        // TODO: for forbidden comment
        if (null != originalComment) {
            LOGGER.log(Level.FINEST, "Comment[id={0}]", originalCommentId);
            final String cmterId =
                    originalComment.getString(Comment.COMMENTER_ID);
            final JSONObject cmter = userRepository.get(cmterId);
            final String cmterName = cmter.getString(User.USER_NAME);
            final String cmterURL = cmter.getString(User.USER_URL);
            final String originalCmtContent =
                    originalComment.getString(Comment.COMMENT_CONTENT);

            repliesBuilder.append(BEGIN_CMT_REF_HTML).append("by ");
            final boolean hasCmterURL = !Strings.isEmptyOrNull(cmterURL);
            if (hasCmterURL) {
                repliesBuilder.append("<a href='http://").append(cmterURL).
                        append("' target='_blank'>");
            }
            repliesBuilder.append(cmterName);
            if (hasCmterURL) {
                repliesBuilder.append("</a>");
            }
            repliesBuilder.append("<br/>").append(originalCmtContent);

            final String moreOriginalCmtId = originalComment.optString(
                    Comment.COMMENT_ORIGINAL_COMMENT_ID);
            composeReplies(moreOriginalCmtId, repliesBuilder);

            repliesBuilder.append(END_CMT_REF_HTML);
        }
    }
}
