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
import org.b3log.latke.model.User;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.repository.TagRepository;
import org.b3log.symphony.repository.TagUserRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.TagGAERepository;
import org.b3log.symphony.repository.impl.TagUserGAERepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.b3log.symphony.util.Langs;
import org.json.JSONObject;

/**
 * Index action. index.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Feb 9, 2011
 */
public final class IndexAction extends AbstractCacheablePageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(IndexAction.class.getName());
    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagGAERepository.getInstance();
    /**
     * Tag-User repository.
     */
    private TagUserRepository tagUserRepository = TagUserGAERepository.
            getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            ret.putAll(Langs.all());

            // Tags
            final int maxTagCnt = 10;
            final int maxAuthorCnt = 3;
            final int maxArticleCnt = 3;
            final List<JSONObject> tags =
                    tagRepository.getMostUsedTags(maxTagCnt);
            List<JSONObject> articles = null;
            LOGGER.log(Level.FINE, "Getting tags....");
            for (int i = 0; i < tags.size(); i++) {
                final JSONObject tag = tags.get(i);
                final String tagId = tag.getString(Keys.OBJECT_ID);
                final String tagTitle = tag.getString(Tag.TAG_TITLE);

                LOGGER.log(Level.FINE, "Getting top authors for tag[title={0}]",
                           tagTitle);
                final List<JSONObject> topAuthors = new ArrayList<JSONObject>();
                tag.put(Tag.TAG_TOP_AUTHORS_REF, (Object) topAuthors);
                final List<String> topAuthorIds =
                        tagUserRepository.getTopTagUsers(tagId, maxAuthorCnt);
                for (final String topAuthorId : topAuthorIds) {
                    final JSONObject user = userRepository.get(topAuthorId);

                    final JSONObject topAuthor = new JSONObject();
                    topAuthor.put(Keys.OBJECT_ID, topAuthorId);
                    final String topAuthorName = user.getString(User.USER_NAME);
                    topAuthor.put(User.USER_NAME, topAuthorName);
                    final String topAuthorURL = user.getString(User.USER_URL);
                    topAuthor.put(User.USER_URL, topAuthorURL);
                    topAuthors.add(topAuthor);
                }
                LOGGER.log(Level.FINE, "Got top authors for tag[title={0}]",
                           tagTitle);

                LOGGER.log(Level.FINE,
                           "Getting recent articles for tag[title={0}]",
                           tagTitle);
                articles = tagRepository.getRecentArticles(tagTitle,
                                                           maxArticleCnt);
                for (final JSONObject article : articles) {
                    final String authorId = article.getString(
                            Common.AUTHOR_ID);
                    final JSONObject author = userRepository.get(authorId);
                    final String name = author.getString(User.USER_NAME);
                    article.put(Article.ARTICLE_AUTHOR_NAME_REF, name);
                    final String url = author.getString(User.USER_URL);
                    article.put(Article.ARTICLE_AUTHOR_URL_REF, url);
                    final String sign = author.getString(Common.SIGN);
                    article.put(Common.SIGN, sign);
                }
                tag.put(Tag.TAG_ARTICLES_REF, (Object) articles);
                LOGGER.log(Level.FINE, "Got recent articles for tag[title={0}]",
                           tagTitle);
            }
            LOGGER.log(Level.FINE, "Got tags");

            ret.put(Tag.TAGS, tags);
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
}
