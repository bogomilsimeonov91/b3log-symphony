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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.TagArticleRepository;
import org.b3log.symphony.repository.TagRepository;
import org.b3log.symphony.repository.impl.ArticleGAERepository;
import org.b3log.symphony.repository.impl.TagArticleGAERepository;
import org.b3log.symphony.repository.impl.TagGAERepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Gets news.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 6, 2011
 */
public final class GetNewsServlet extends HttpServlet {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(GetNewsServlet.class.getName());
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagGAERepository.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository =
            TagArticleGAERepository.getInstance();
    /**
     * News fetch size.
     */
    private static final int NEWS_FETCH_SIZE = 10;

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        final String callback = request.getParameter("callback");

        final JSONObject ret = new JSONObject();
        final List<JSONObject> articles = new ArrayList<JSONObject>();
        try {
            final JSONObject tag =
                    tagRepository.getByTitle("B3log Announcement");

            if (null == tag) {
                LOGGER.info("Not found B3log Announcement");
                return;
            }

            final String tagId = tag.getString(Keys.OBJECT_ID);
            final JSONObject result =
                    tagArticleRepository.getByTagId(tagId, 1,
                                                    Integer.MAX_VALUE);
            final JSONArray tagArticleRelations =
                    result.getJSONArray(Keys.RESULTS);

            LOGGER.log(Level.FINE, "Getting articles....");
            for (int i = 0; i < tagArticleRelations.length(); i++) {
                final JSONObject tagArticleRel =
                        tagArticleRelations.getJSONObject(i);
                final JSONObject article =
                        articleRepository.get(tagArticleRel.getString(
                        Article.ARTICLE + "_" + Keys.OBJECT_ID));

                final String authorId = article.getString(
                        Common.AUTHOR_ID);
                if ("1297514560963".equals(authorId)) { // Author is 88250 ;-)
                    articles.add(article);
                }

                if (articles.size() >= NEWS_FETCH_SIZE) {
                    break;
                }
            }

            LOGGER.log(Level.FINE, "Got articles[size={0}]", articles.size());

            ret.put(Article.ARTICLES, articles);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        final PrintWriter printWriter = response.getWriter();

        printWriter.print(callback + "(" + ret.toString() + ")");
        printWriter.close();
    }
}
