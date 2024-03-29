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
import org.b3log.symphony.action.util.Filler;
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
import org.b3log.symphony.util.Symphonys;
import org.b3log.symphony.util.Users;
import org.json.JSONObject;

/**
 * Top entries action. top-entries.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Feb 17, 2011
 */
public final class TopEntriesAction extends AbstractCacheablePageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TopEntriesAction.class.getName());
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
    /**
     * Top tags count.
     */
    public static final int TOP_TAGS_CNT = Symphonys.getInt("topTagsCnt");
    /**
     * Top entries count per tag.
     */
    public static final int TOP_ENTRIES_CNT_PER_TAG =
            Symphonys.getInt("topEntriesCntPerTag");
    /**
     * Top tag user count.
     */
    public static final int TOP_TAG_USER_CNT =
            Symphonys.getInt("topTagUserCnt");

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            ret.putAll(Langs.all());

            // Tags
            final List<JSONObject> tags =
                    tagRepository.getMostUsedTags(TOP_TAGS_CNT);
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
                        tagUserRepository.getTopTagUsers(tagId,
                                                         TOP_TAG_USER_CNT);
                for (final String topAuthorId : topAuthorIds) {
                    final JSONObject user = userRepository.get(topAuthorId);

                    final JSONObject topAuthor = new JSONObject();
                    topAuthor.put(Keys.OBJECT_ID, topAuthorId);
                    final String topAuthorName = user.getString(User.USER_NAME);
                    topAuthor.put(User.USER_NAME, topAuthorName);
                    final String topAuthorURL = user.getString(User.USER_URL);
                    topAuthor.put(User.USER_URL, topAuthorURL);
                    topAuthor.put(Common.USER_THUMBNAIL_URL,
                                  user.getString(Common.USER_THUMBNAIL_URL));
                    topAuthors.add(topAuthor);
                }
                LOGGER.log(Level.FINE, "Got top authors for tag[title={0}]",
                           tagTitle);

                LOGGER.log(Level.FINE,
                           "Getting top comment articles for tag[title={0}]",
                           tagTitle);
                articles = tagRepository.getTopArticles(tagTitle,
                                                        TOP_ENTRIES_CNT_PER_TAG);
                for (final JSONObject article : articles) {
                    final String authorId = article.getString(
                            Common.AUTHOR_ID);
                    final JSONObject author = userRepository.get(authorId);
                    final String name = author.getString(User.USER_NAME);
                    article.put(Article.ARTICLE_AUTHOR_NAME_REF, name);
                    final String url = author.getString(User.USER_URL);
                    article.put(Article.ARTICLE_AUTHOR_URL_REF, url);
                    final String sign = Users.getUserSignHTML(author);
                    article.put(Common.SIGN, sign);
                    article.put(Article.ARTICLE_AUTHOR_THUMBNAIL_URL_REF,
                                author.getString(Common.USER_THUMBNAIL_URL));
                }
                tag.put(Tag.TAG_ARTICLES_REF, (Object) articles);
                LOGGER.log(Level.FINE,
                           "Got top comment articles for tag[title={0}]",
                           tagTitle);
            }
            LOGGER.log(Level.FINE, "Got tags");

            ret.put(Tag.TAGS, tags);

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
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
