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

import com.dlog4j.util.UBBDecoder;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Strings;
import org.b3log.symphony.SymphonyServletListener;
import org.b3log.symphony.event.EventTypes;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Comment;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Session;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.repository.ArticleCommentRepository;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.CommentRepository;
import org.b3log.symphony.repository.TagRepository;
import org.b3log.symphony.repository.impl.ArticleCommentGAERepository;
import org.b3log.symphony.repository.impl.ArticleGAERepository;
import org.b3log.symphony.repository.impl.CommentGAERepository;
import org.b3log.symphony.repository.impl.TagGAERepository;
import org.b3log.symphony.util.Articles;
import org.b3log.symphony.util.Langs;
import org.b3log.symphony.util.Symphonys;
import org.b3log.symphony.util.TimeZones;
import org.b3log.symphony.util.Users;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Adds entry comment submitted locally.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Feb 22, 2011
 */
public final class UserAddEntryCommentAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(UserAddEntryCommentAction.class.getName());
    /**
     * Comment repository.
     */
    private static CommentRepository commentRepository =
            CommentGAERepository.getInstance();
    /**
     * Article utilities.
     */
    private static Articles articleUtils = Articles.getInstance();
    /**
     * Time zone utilities.
     */
    private static TimeZones timeZoneUtils = TimeZones.getInstance();
    /**
     * Article repository.
     */
    private static ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Tag repository.
     */
    private static TagRepository tagRepository =
            TagGAERepository.getInstance();
    /**
     * Article-Comment repository.
     */
    private static ArticleCommentRepository articleCommentRepository =
            ArticleCommentGAERepository.getInstance();
    /**
     * Event manager.
     */
    private EventManager eventManager = EventManager.getInstance();

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Adds a comment to an article.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": articleId,
     *     "commentContent": "", // by UBB
     *     "commentOriginalCommentId": "" // optional, if exists this key, the comment
     *                                    // is an reply
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "oId": generatedCommentId,
     *     "sc": true, // true for succeed
     *     "commentDate": "", // yyyy/MM/dd hh:mm:ss
     *     "commentSharpURL": "",
     *     "commentOriginalCommentName": "" // if exists this key, the comment is an reply
     * }
     * </pre>
     * @throws ActionException action exception
     */
    @Override
    public JSONObject doAjaxAction(final JSONObject requestJSONObject,
                                   final HttpServletRequest request,
                                   final HttpServletResponse response)
            throws ActionException {
        final JSONObject ret = new JSONObject();

        final HttpSession session = request.getSession();
        Long latestPostTIme =
                (Long) session.getAttribute(Session.LATEST_POST_TIME);
        final Long currentPostTime = System.currentTimeMillis();
        if (null == latestPostTIme) {
            latestPostTIme = 0L;
        }

        final long minStepPostTime = Long.valueOf(
                Symphonys.get("minStepPostTime"));
        LOGGER.log(Level.FINER,
                   "Current post time[{0}], the latest post time[{1}]",
                   new Object[]{currentPostTime, latestPostTIme});
        try {
            if (latestPostTIme > (currentPostTime - minStepPostTime)) {
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG, Langs.get("postTooFrequentLabel"));

                return ret;
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        latestPostTIme = currentPostTime;

        String commenterName = null;
        String commenterId = null;
        String articleId = null;
        String commentContent = null;

        final JSONObject commenter = Users.getCurrentUser();

        final Transaction transaction = commentRepository.beginTransaction();
        String commentId;
        try {
            commenterId = commenter.getString(Keys.OBJECT_ID);
            commenterName = commenter.getString(User.USER_NAME);

            articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            commentContent =
                    requestJSONObject.getString(Comment.COMMENT_CONTENT);

            final JSONObject article = articleRepository.get(articleId);
            commentContent = commentContent.replaceAll("<", "&lt;").
                    replaceAll(">", "&gt;");
            commentContent = UBBDecoder.decode(commentContent);
            final String originalCommentId = requestJSONObject.optString(
                    Comment.COMMENT_ORIGINAL_COMMENT_ID);

            // Add comment
            final JSONObject comment = new JSONObject();
            JSONObject originalComment = null;
            comment.put(Comment.COMMENTER_ID, commenterId);
            comment.put(Comment.COMMENT_ENTRY_ID, articleId);
            comment.put(Comment.COMMENT_ENTRY_TITLE,
                        article.getString(Article.ARTICLE_TITLE));
            final Date date = timeZoneUtils.getTime(
                    SymphonyServletListener.TIME_ZONE_ID);
            comment.put(Comment.COMMENT_DATE, date);
            comment.put(Common.STATE, Common.AVAILABLE);

            article.put(Article.ARTICLE_LAST_CMT_DATE, date);
            articleRepository.update(articleId, article);

            final String tagsString = article.getString(Article.ARTICLE_TAGS);
            final String[] tagStrings = tagsString.split(",");
            for (int i = 0; i < tagStrings.length; i++) {
                final String tagTitle = tagStrings[i];
                final JSONObject tag = tagRepository.getByTitle(tagTitle);
                final int cmtCnt = tag.getInt(Tag.TAG_COMMENT_COUNT);
                tag.put(Tag.TAG_COMMENT_COUNT, cmtCnt + 1);

                final String tagId = tag.getString(Keys.OBJECT_ID);
                tagRepository.update(tagId, tag);
            }

            ret.put(Comment.COMMENT_DATE, Comment.DATE_FORMAT.format(date));
            if (!Strings.isEmptyOrNull(originalCommentId)) {
                originalComment =
                        commentRepository.get(originalCommentId);
                if (null != originalComment) {
                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID,
                                originalCommentId);
                    comment.put(Comment.COMMENT_ORIGINAL_CMTER_ID,
                            originalComment.getString(Comment.COMMENTER_ID));
                } else {
                    LOGGER.log(Level.WARNING,
                               "Not found orginal comment[id={0}] of reply[name={1}, content={2}]",
                               new String[]{originalCommentId, commenterName,
                                            commentContent});
                }
            }
            comment.put(Comment.COMMENT_CONTENT, commentContent);

            commentId = commentRepository.add(comment);
            // Save comment sharp URL
            final String commentSharpURL =
                    getCommentSharpURLForArticle(article,
                                                 commentId);
            comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
            ret.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
            comment.put(Keys.OBJECT_ID, commentId);
            commentRepository.update(commentId, comment);
            // Add article-comment relation
            final JSONObject articleCommentRelation = new JSONObject();
            articleCommentRelation.put(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                                       articleId);
            articleCommentRelation.put(Comment.COMMENT + "_" + Keys.OBJECT_ID,
                                       commentId);
            articleCommentRelation.put(Article.ARTICLE_COMMENT_COUNT,
                                       article.getInt(
                    Article.ARTICLE_COMMENT_COUNT) + 1);
            articleCommentRepository.add(articleCommentRelation);
            // Update article comment
            articleUtils.updateArticleComment(articleId, comment);

            PageCaches.removeAll();

            transaction.commit();
            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.OBJECT_ID, commentId);

            session.setAttribute(Session.LATEST_POST_TIME, latestPostTIme);

            final JSONObject eventData = new JSONObject();
            eventData.put(Comment.COMMENT, comment);
            eventData.put(Article.ARTICLE, article);
            eventManager.fireEventSynchronously(
                    new Event<JSONObject>(EventTypes.ADD_COMMENT_TO_ARTICLE,
                                          eventData));
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets comment sharp URL with the specified article and comment id.
     *
     * @param article the specified article
     * @param commentId the specified comment id
     * @return comment sharp URL
     * @throws JSONException json exception
     */
    private static String getCommentSharpURLForArticle(final JSONObject article,
                                                       final String commentId)
            throws JSONException {
        final String articleLink = article.getString(Article.ARTICLE_PERMALINK);

        return articleLink + "#" + commentId;
    }
}
