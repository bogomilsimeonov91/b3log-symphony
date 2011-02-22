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
import org.b3log.latke.util.Strings;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Comment;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Message;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;
import org.jsoup.Jsoup;

/**
 * This listener is responsible for processing comment reply.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Feb 22, 2011
 */
public final class CommentNotifier
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CommentNotifier.class.getName());
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();
    /**
     * URL fetch service.
     */
    private static final URLFetchService URL_FETCH_SVC =
            URLFetchServiceFactory.getURLFetchService();

    /**
     * Constructs a {@link ArticleCommentReplyNotifier} object with the
     * specified event manager.
     *
     * @param eventManager the specified event manager
     */
    public CommentNotifier(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject eventData = event.getData();
        final JSONObject comment = eventData.optJSONObject(Comment.COMMENT);
        final JSONObject article = eventData.optJSONObject(Article.ARTICLE);
        LOGGER.log(Level.FINER,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                eventData,
                                CommentNotifier.class.getName()});
        try {
            final String commenterId = comment.getString(Comment.COMMENTER_ID);
            final JSONObject commenter = userRepository.get(commenterId);
            final String commenterQQNum =
                    commenter.optString(Common.USER_QQ_NUM);
            if (Strings.isEmptyOrNull(commenterQQNum)) {
                return;
            }

            final String commentContentHTML =
                    comment.getString(Comment.COMMENT_CONTENT);
            final String contentText = Jsoup.parse(commentContentHTML).text();
            final StringBuilder contentBuilder = new StringBuilder(contentText);
            final String commentSharpURL =
                    comment.getString(Comment.COMMENT_SHARP_URL);
            contentBuilder.append("\r\n").append(commentSharpURL);
//            final String articleTitle = article.getString(Article.ARTICLE_TITLE);
//            final String articleLink = "http://" + Symphonys.HOST + article.
//                    getString(Article.ARTICLE_PERMALINK);
            final String imServerIP = Symphonys.get("imServerIP");
            final String imServerPort = Symphonys.get("imServerPort");
            final URL imServiceURL =
                    new URL("http://" + imServerIP + ":" + imServerPort + "/add");
            final HTTPRequest httpRequest =
                    new HTTPRequest(imServiceURL, HTTPMethod.PUT);
            
            final JSONObject requestJSONObject = new JSONObject();
            requestJSONObject.put("key", Symphonys.get("keyOfSymphony"));
            requestJSONObject.put(Message.MESSAGE_PROCESSOR, "QQ");
            requestJSONObject.put(Message.MESSAGE_CONTENT,
                    contentBuilder.toString());
            requestJSONObject.put(Message.MESSAGE_TO_ACCOUNT, commenterQQNum);

            final byte[] payload = requestJSONObject.toString().getBytes();
            httpRequest.setPayload(payload);

            URL_FETCH_SVC.fetchAsync(httpRequest);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new EventException("Reply notifier error!");
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
