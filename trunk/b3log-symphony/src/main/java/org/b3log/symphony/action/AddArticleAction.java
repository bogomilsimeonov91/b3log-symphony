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

import java.util.Enumeration;
import java.util.logging.Level;
import org.b3log.latke.action.ActionException;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.repository.Transaction;
import org.b3log.symphony.SymphonyServletListener;
import static org.b3log.symphony.model.Article.*;
import org.b3log.symphony.model.Solo;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.ArticleGAERepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.b3log.symphony.util.Articles;
import org.b3log.symphony.util.Tags;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Adds articles submitted from B3log Solo.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jan 28, 2011
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
    private ArticleRepository articleRepository = ArticleGAERepository.
            getInstance();
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
     * Version of latest Solo.
     */
    private static final String LATEST_SOLO_VERSION = "0.2.5";

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
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
     *         "articleTitle": "",
     *         "articlePermalink": "",
     *         "articleTags": "tag1, tag2, ....",
     *         "articleAuthorEmail": "",
     *         "articleContent": ""
     *     },
     *     "blogHost": "",
     *     "soloVersion": ""
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "ADD_ARTICLE_SUCC"
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
        final Transaction transaction = articleRepository.beginTransaction();

        try {
            if (LOGGER.isLoggable(Level.FINER)) {
                logRequestHeaders(request);
            }

            final String soloHost = data.getString(Solo.SOLO_HOST);
            final String soloVersion = data.optString(Solo.SOLO_VERSION);

            LOGGER.log(Level.INFO,
                       "Data[{0}] come from Solo[host={1}, version={2}]",
                       new String[]{data.toString(
                        SymphonyServletListener.JSON_PRINT_INDENT_FACTOR),
                                    soloHost, soloVersion});

            final JSONObject originalArticle = data.getJSONObject(ARTICLE);

            final JSONObject article = new JSONObject();
            final String title = originalArticle.getString(ARTICLE_TITLE);
            article.put(ARTICLE_TITLE, title);
            final String tagString = originalArticle.getString(ARTICLE_TAGS);
            article.put(ARTICLE_TAGS, tagString);
            final String permalink = "http://" + soloHost + originalArticle.
                    getString(ARTICLE_PERMALINK);
            article.put(ARTICLE_PERMALINK, permalink);
            final String content = originalArticle.getString(ARTICLE_CONTENT);
            article.put(ARTICLE_CONTENT, content);
            
            article.put(Solo.SOLO_HOST, soloHost);
            article.put(Solo.SOLO_VERSION, soloVersion);

            final String authorEmail =
                    originalArticle.getString(ARTICLE_AUTHOR_EMAIL);
            final JSONObject author = userRepository.getByEmail(authorEmail);
            if (null != author) {// The author has related with Symphony
                final String authorId = author.getString(Keys.OBJECT_ID);
                article.put(ARTICLE_AUTHOR_ID, authorId);
            }
            article.put(ARTICLE_AUTHOR_EMAIL, authorEmail);

            articleRepository.add(article);

            final String[] tagTitles = tagString.split(",");
            final JSONArray tags = tagUtils.tag(tagTitles, article);
            articleUtils.addTagArticleRelation(tags, article);

            transaction.commit();

            ret.put(Keys.STATUS_CODE, "succ");

            return ret;
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe(e.getMessage());

            try {
                ret.put(Keys.STATUS_CODE, "failed");
            } catch (final JSONException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new ActionException(ex);
            }

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
