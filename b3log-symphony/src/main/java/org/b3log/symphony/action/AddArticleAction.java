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

import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import org.b3log.latke.action.ActionException;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Strings;
import org.b3log.symphony.model.Article;
import static org.b3log.symphony.model.Article.*;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.ArticleGAERepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.b3log.symphony.util.Articles;
import org.b3log.symphony.util.Errors;
import org.b3log.symphony.util.Langs;
import org.b3log.symphony.util.Symphonys;
import org.b3log.symphony.util.Tags;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Adds articles submitted from <a href="http://b3log-rhythm.googlecode.com">
 * B3log Rhythm</a>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Sep 4, 2011
 */
public final class AddArticleAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AddArticleAction.class.getName());
    /**
     * Event manager.
     */
    private EventManager eventManager = EventManager.getInstance();
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

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Adds article.
     *
     * @param data for example,
     * <pre>
     * {
     *     "article": {
     *         "articleOriginalId": "", 
     *         "articleTitle": "",
     *         "articlePermalink": "",
     *         "articleTags": "tag1, tag2, ....",
     *         "articleAuthorEmail": "",
     *         "articleContent": ""
     *         "articleCreateDate": long
     *     },
     *     "key": "",
     *     "title": "",
     *     "host": "",
     *     "from": "",
     *     "version": "",
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
        if (!Symphonys.runsOnDevEnv()) {
            final String key = data.optString("key");
            if (Strings.isEmptyOrNull(key)) {
                try {
                    Errors.sendError(request, response,
                                     HttpServletResponse.SC_FORBIDDEN,
                                     request.getRequestURI(), Langs.get(
                            "forbiddenLabel"));
                } catch (final Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    throw new ActionException(e);
                }

                return null;
            }
        }

        final JSONObject ret = new JSONObject();
        final Transaction transaction = articleRepository.beginTransaction();

        try {
            if (LOGGER.isLoggable(Level.FINER)) {
                logRequestHeaders(request);
            }

            final String from = data.getString(Common.FROM);
            final String title = data.getString(Common.TITLE);
            final String version = data.optString(Common.VERSION);
            final String host = data.getString(Common.HOST);

            LOGGER.log(Level.INFO,
                       "Data[title={0}, host={1}, version={2}, from={3}]",
                       new String[]{title, host, version, from});

            final JSONObject originalArticle = data.getJSONObject(ARTICLE);
            final JSONObject article = new JSONObject();
            final String originalId =
                    originalArticle.getString(ARTICLE_ORIGINAL_ID);
            article.put(ARTICLE_ORIGINAL_ID, originalId);
            final String authorEmail =
                    originalArticle.getString(ARTICLE_AUTHOR_EMAIL_REF).
                    toLowerCase();
            final JSONObject author = userRepository.getByEmail(authorEmail);
            if (null != author) {
                final String authodId = author.getString(Keys.OBJECT_ID);
                article.put(Common.AUTHOR_ID, authodId);
            } else {
                LOGGER.warning("Unauthorized request, register an account first");
                Errors.sendError(request, response,
                                 HttpServletResponse.SC_FORBIDDEN,
                                 "/add-article",
                                 Langs.get("loginFirstLabel"));
                return ret;
            }

            article.put(ARTICLE_TITLE, originalArticle.getString(ARTICLE_TITLE));
            final String tagString = originalArticle.getString(ARTICLE_TAGS);
            article.put(ARTICLE_TAGS, Tags.removeWhitespaces(tagString));
            final String permalink = "http://" + host + originalArticle.getString(ARTICLE_PERMALINK);
            article.put(ARTICLE_PERMALINK, permalink);
            article.put(ARTICLE_CONTENT,
                        originalArticle.getString(ARTICLE_CONTENT));
            final long createDateTime =
                    originalArticle.optLong(Article.ARTICLE_CREATE_DATE);
            Date createDate = null;
            if (0 == createDateTime) {
                createDate = new Date();
            } else {
                createDate = new Date(createDateTime);
            }
            article.put(Article.ARTICLE_CREATE_DATE, createDate);
            article.put(Article.ARTICLE_LAST_CMT_DATE, new Date(0));
            article.put(Article.ARTICLE_COMMENT_COUNT, 0);
            article.put(Common.STATE, Common.AVAILABLE);
            article.put(Common.FROM_TITLE, title);
            article.put(Article.ARTICLE_FROM, from);
            article.put(Common.HOST, host);
            article.put(Common.VERSION, version);

            articleRepository.add(article);

            final String[] tagTitles = tagString.split(",");
            final JSONArray tags = tagUtils.tag(tagTitles, article);
            tagUtils.updateTagUserRelation(tags, author);
            articleUtils.addTagArticleRelation(tags, article);

            transaction.commit();

            CacheFactory.getCache("").removeAll(); // TODO: just clear all caches

            ret.put(Keys.STATUS_CODE, true);

            return ret;
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            ret.put(Keys.STATUS_CODE, false);
            ret.put(Keys.MSG, e.getMessage());

            return ret;
        }
    }

    /**
     * Logs the headers of the specified request.
     *
     * @param request the specified request
     */
    private void logRequestHeaders(final HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        final Enumeration<String> enames = request.getHeaderNames();
        while (enames.hasMoreElements()) {
            final String name = enames.nextElement();
            final String value = request.getHeader(name);
            LOGGER.log(Level.FINER, "Request header[name={0}, value={1}]",
                       new Object[]{name, value});
        }
    }
}
