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

package org.b3log.symphony.event;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.model.User;
import org.b3log.latke.util.Strings;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Comment;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.json.JSONObject;

/**
 * This listener is responsible for sending comment to B3log Solo.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 26, 2011
 */
public final class CommentSender
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CommentSender.class.getName());
    /**
     * URL fetch service.
     */
    private final URLFetchService urlFetchService =
            URLFetchServiceFactory.getURLFetchService();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();

    /**
     * Constructs a {@link ArticleSender} object with the specified event
     * manager.
     *
     * @param eventManager the specified event manager
     */
    public CommentSender(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject data = event.getData();
        LOGGER.log(Level.FINER,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                data,
                                CommentSender.class.getName()});
        try {
            final JSONObject comment =
                    data.getJSONObject(Comment.COMMENT);
            final JSONObject article =
                    data.getJSONObject(Article.ARTICLE);
            final String authorId =
                    article.getString(Article.ARTICLE_AUTHOR_ID);
            final JSONObject author = userRepository.get(authorId);
            final String keyOfSolo = author.optString(Common.KEY_OF_SOLO);
            final String articleId =
                    article.optString(Article.ARTICLE_ORIGINAL_ID);
            final String authorURL = author.optString(User.USER_URL);
            if (Strings.isEmptyOrNull(keyOfSolo)
                || Strings.isEmptyOrNull(authorURL)
                || Strings.isEmptyOrNull(articleId)) {
                // The author does not set the Solo key
                // or the author does not set the Solo URL
                // or this article is not added from Solo
                return;
            }

            final URL url = new URL("http://" + authorURL
                    + "/add-article-from-symphony-comment.do");
            final HTTPRequest httpRequest =
                    new HTTPRequest(url, HTTPMethod.POST);
            final JSONObject requestJSONObject = new JSONObject();
            requestJSONObject.put(Common.KEY_OF_SOLO, keyOfSolo);
            requestJSONObject.put(Article.ARTICLE_ID_REF, articleId);
            requestJSONObject.put(Comment.COMMENTER_NAME_REF,
                                  comment.getString(Comment.COMMENTER_NAME_REF));
            requestJSONObject.put(Comment.COMMENTER_EMAIL_REF,
                                  comment.getString(Comment.COMMENTER_EMAIL_REF));
            requestJSONObject.put(Comment.COMMENTER_URL_REF,
                                  comment.getString(Comment.COMMENTER_URL_REF));
            requestJSONObject.put(Comment.COMMENT_CONTENT,
                                  comment.getString(Comment.COMMENT_CONTENT));
            httpRequest.setPayload(
                    requestJSONObject.toString().getBytes("UTF-8"));
//            final Future<HTTPResponse> futureResponse =
            urlFetchService.fetchAsync(httpRequest);
//            final HTTPResponse httpResponse =
//                    futureResponse.get(TIMEOUT, TimeUnit.MILLISECONDS);
//            final int statusCode = httpResponse.getResponseCode();
//            LOGGER.log(Level.FINEST, "Response from Rhythm[statusCode={0}]",
//                       statusCode);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Sends comment to Solo error: {0}",
                       e.getMessage());
            throw new EventException(e);
        }
    }

    /**
     * Gets the event type {@linkplain EventTypes#ADD_COMMENT_TO_ARTICLE}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.ADD_COMMENT_TO_ARTICLE;
    }
}
