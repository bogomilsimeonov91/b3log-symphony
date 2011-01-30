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
import org.b3log.latke.event.EventManager;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Transaction;
import org.b3log.symphony.model.Article;
import static org.b3log.symphony.model.Article.*;
import org.b3log.symphony.model.Blog;
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
 * @version 1.0.0.2, Jan 30, 2011
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
     *         "articleCreateDate": java.util.Date
     *     },
     *     "blogHost": "",
     *     "blogTitle": "",
     *     "blogVersion": "",
     *     "blog": ""
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

            final String blog = data.getString(Blog.BLOG);
            final String blogTitle = data.getString(Blog.BLOG_TITLE);
            final String blogHost = data.getString(Blog.BLOG_HOST);
            final String blogVersion = data.optString(Blog.BLOG_VERSION);

            LOGGER.log(Level.INFO,
                       "Data come from [blog={1}, host={2}, version={3}]",
                       new String[]{blogTitle, blogHost, blogVersion});

            final JSONObject originalArticle = data.getJSONObject(ARTICLE);

            final JSONObject article = new JSONObject();
            article.put(ARTICLE_TITLE, originalArticle.getString(ARTICLE_TITLE));
            final String tagString = originalArticle.getString(ARTICLE_TAGS);
            article.put(ARTICLE_TAGS, tagString);
            final String permalink = "http://" + blogHost + originalArticle.
                    getString(ARTICLE_PERMALINK);
            article.put(ARTICLE_PERMALINK, permalink);
            article.put(ARTICLE_CONTENT,
                        originalArticle.getString(ARTICLE_CONTENT));
            Date createDate =
                    (Date) originalArticle.opt(Article.ARTICLE_CREATE_DATE);
            createDate = null != createDate ? createDate : new Date();
            article.put(Article.ARTICLE_CREATE_DATE, createDate);
            article.put(Article.ARTICLE_COMMENT_COUNT, 0);
            article.put(Blog.BLOG, blog);
            article.put(Blog.BLOG_HOST, blogHost);
            article.put(Blog.BLOG_VERSION, blogVersion);

            final String authorEmail =
                    originalArticle.getString(ARTICLE_AUTHOR_EMAIL);
            final JSONObject author = userRepository.getByEmail(authorEmail);
            if (null != author) {// The author has related with Symphony
                final String authorName = author.getString(User.USER_NAME);
                article.put(ARTICLE_AUTHOR_NAME, authorName);
            } else {
                article.put(Article.ARTICLE_AUTHOR_NAME, blogTitle);
            }

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
