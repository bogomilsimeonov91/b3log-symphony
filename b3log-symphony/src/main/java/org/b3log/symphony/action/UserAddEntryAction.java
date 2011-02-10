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
import java.util.HashMap;
import java.util.logging.Level;
import org.b3log.latke.action.ActionException;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Ids;
import org.b3log.symphony.model.Article;
import static org.b3log.symphony.model.Article.*;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.TagRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.ArticleGAERepository;
import org.b3log.symphony.repository.impl.TagGAERepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.b3log.symphony.util.Articles;
import org.b3log.symphony.util.Errors;
import org.b3log.symphony.util.Langs;
import org.b3log.symphony.util.Tags;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Adds entry submitted locally.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 10, 2011
 */
public final class UserAddEntryAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(UserAddEntryAction.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();
    /**
     * Tag utilities.
     */
    private Tags tagUtils = Tags.getInstance();
    /**
     * Article utilities.
     */
    private Articles articleUtils = Articles.getInstance();
    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagGAERepository.getInstance();

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        ret.putAll(Langs.all());

       try {
            final HttpSession session = request.getSession();
            if (null == session) {
                final String cause = Langs.get(
                        "loginFirstLabel");
                Errors.sendError(request, response,
                                 HttpServletResponse.SC_FORBIDDEN,
                                 request.getRequestURI(), cause);

                return ret;
            }

            final String email = (String) session.getAttribute(User.USER_EMAIL);
            if (null == email) {
                final String cause = Langs.get(
                        "loginFirstLabel");
                Errors.sendError(request, response,
                                 HttpServletResponse.SC_FORBIDDEN,
                                 request.getRequestURI(), cause);

                return ret;
            }
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);

            try {
                final String cause = Langs.get("forbiddenLabel");
                Errors.sendError(request, response,
                                 HttpServletResponse.SC_FORBIDDEN,
                                 "/file", cause);

                return ret;
            } catch (final Exception ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new ActionException(e);
            }
        }

        final StringBuilder stringBuilder = new StringBuilder("[");
        try {
            final JSONObject result =
                    tagRepository.get(new Query());
            final JSONArray tagArray = result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < tagArray.length(); i++) {
                final JSONObject tag = tagArray.getJSONObject(i);
                final String tagTitle = "\"" + tag.get(Tag.TAG_TITLE) + "\"";
                stringBuilder.append(tagTitle);
                if (i < tagArray.length() - 1) {
                    stringBuilder.append(",");
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        stringBuilder.append("]");

        ret.put(Tag.TAGS, stringBuilder.toString());

        return ret;
    }

    /**
     * Adds article.
     *
     * @param data for example,
     * <pre>
     * {
     *     "article": {
     *         "articleTitle": "",
     *         "articleTags": "tag1, tag2, ....",
     *         "articleAuthorEmail": "",
     *         "articleContent": "" // by UBB
     *     }
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": true
     * }
     * </pre>
     * @throws ActionException action exception
     */
    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        final JSONObject ret = new JSONObject();
        try {
            final HttpSession session = request.getSession();
            if (null == session) {
                final String cause = Langs.get(
                        "loginFirstLabel");
                Errors.sendError(request, response,
                                 HttpServletResponse.SC_FORBIDDEN,
                                 request.getRequestURI(), cause);

                return ret;
            }

            final String email = (String) session.getAttribute(User.USER_EMAIL);
            if (null == email) {
                final String cause = Langs.get(
                        "loginFirstLabel");
                Errors.sendError(request, response,
                                 HttpServletResponse.SC_FORBIDDEN,
                                 request.getRequestURI(), cause);

                return ret;
            }
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);

            try {
                final String cause = Langs.get("forbiddenLabel");
                Errors.sendError(request, response,
                                 HttpServletResponse.SC_FORBIDDEN,
                                 "/file", cause);
            } catch (final Exception ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new ActionException(e);
            }
            return ret;
        }

        final Transaction transaction = articleRepository.beginTransaction();

        try {
            final JSONObject originalArticle = data.getJSONObject(ARTICLE);
            final JSONObject article = new JSONObject();
            final String articleId = Ids.genTimeMillisId();
            article.put(Keys.OBJECT_ID, articleId);

            final String authorEmail =
                    originalArticle.getString(ARTICLE_AUTHOR_EMAIL_REF).
                    toLowerCase();
            final JSONObject author = userRepository.getByEmail(authorEmail);
            if (null != author) {
                final String authodId = author.getString(Keys.OBJECT_ID);
                article.put(Common.AUTHOR_ID, authodId);
            } else {
                Errors.sendError(request, response,
                                 HttpServletResponse.SC_FORBIDDEN,
                                 "/add-entry",
                                 Langs.get("loginFirstLabel"));
                return ret;
            }

            article.put(ARTICLE_TITLE, originalArticle.getString(ARTICLE_TITLE));
            final String tagString = originalArticle.getString(ARTICLE_TAGS);
            article.put(ARTICLE_TAGS, Tags.removeWhitespaces(tagString));
            final String permalink = "http://www.b3log.org/entries/" + articleId;
            article.put(ARTICLE_PERMALINK, permalink);
            String content = originalArticle.getString(ARTICLE_CONTENT);
            content = content.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            content = UBBDecoder.decode(content);
            article.put(ARTICLE_CONTENT, content);
            final long createDateTime = Long.parseLong(articleId);
            article.put(Article.ARTICLE_CREATE_DATE, new Date(createDateTime));
            article.put(Article.ARTICLE_LAST_CMT_DATE, new Date(0));
            article.put(Article.ARTICLE_COMMENT_COUNT, 0);
            article.put(Common.STATE, Common.AVAILABLE);
            article.put(Article.ARTICLE_FROM, "B3log Symphony");
            article.put(Common.HOST, "http://www.b3log.org:80");
            article.put(Common.VERSION, Langs.get("version"));

            articleRepository.add(article);

            final String[] tagTitles = tagString.split(",");
            final JSONArray tags = tagUtils.tag(tagTitles, article);
            tagUtils.updateTagUserRelation(tags, author);
            articleUtils.addTagArticleRelation(tags, article);

            transaction.commit();

            ret.put(Keys.STATUS_CODE, true);

            return ret;
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                ret.put(Keys.STATUS_CODE, false);
            } catch (final JSONException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new ActionException(ex);
            }

            return ret;
        }
    }
}
