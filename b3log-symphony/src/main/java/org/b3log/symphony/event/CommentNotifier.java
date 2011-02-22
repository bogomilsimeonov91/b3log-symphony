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
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import java.net.URL;
import java.util.concurrent.Future;
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
     * IM server IP.
     */
    private static final String IM_SERVER_IP = Symphonys.get("imServerIP");
    /**
     * IM server port.
     */
    private static final String IM_SERVER_PORT = Symphonys.get("imServerPort");
    /**
     * IM server name.
     */
    private static final String IM_SERVER_NAME = Symphonys.get("imServerName");
    /**
     * Key of Symphony.
     */
    private static final String KEY = Symphonys.get("keyOfSymphony");

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
            final String articleAuthorId =
                    article.getString(Common.AUTHOR_ID);
            final JSONObject articleAuthor =
                    userRepository.get(articleAuthorId);
            final String articleAuthorQQNum =
                    articleAuthor.optString(Common.USER_QQ_NUM);
            boolean needToNotifyArticleAuthor = false;
            boolean needToNotifyOriginalCmter = false;

            if (!Strings.isEmptyOrNull(articleAuthorQQNum)) {
                needToNotifyArticleAuthor = true;
            }

            String originalCmterQQNum = null;
            final String originalCmterId =
                    comment.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID);
            if (!Strings.isEmptyOrNull(originalCmterId)) {
                final JSONObject originalCmter =
                        userRepository.get(originalCmterId);
                if (null != originalCmter) {
                    originalCmterQQNum =
                            originalCmter.optString(Common.USER_QQ_NUM);
                }
            }
            if (!Strings.isEmptyOrNull(originalCmterQQNum)) {
                needToNotifyOriginalCmter = true;
            }

            if (!needToNotifyArticleAuthor && !needToNotifyOriginalCmter) {
                return;
            }

            final String commentContentHTML =
                    comment.getString(Comment.COMMENT_CONTENT);
            final String contentText = Jsoup.parse(commentContentHTML).text();
            final StringBuilder contentBuilder = new StringBuilder(contentText);
            final String commentSharpURL =
                    comment.getString(Comment.COMMENT_SHARP_URL);
            contentBuilder.append("\r\n").append(commentSharpURL);

            if (needToNotifyArticleAuthor) {
                notifyArticleAuthor(contentBuilder.toString(),
                                    articleAuthorQQNum);
            }

            if (needToNotifyOriginalCmter) {
                notifyOriginalCmter(contentBuilder.toString(),
                                    originalCmterQQNum);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Notify original commenter with the specified notification content and QQ
     * number of the receiver.
     *
     * @param content the specified notification content
     * @param orignalCmterQQNum the specified notification receiver QQ number
     * @throws Exception exception
     */
    private void notifyOriginalCmter(final String content,
                                     final String orignalCmterQQNum)
            throws Exception {
        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put("key", KEY);
        requestJSONObject.put(Message.MESSAGE_PROCESSOR, "QQ");
        requestJSONObject.put(Message.MESSAGE_CONTENT, content);
        requestJSONObject.put(Message.MESSAGE_TO_ACCOUNT, orignalCmterQQNum);

        final URL imServiceURL =
                new URL("http://" + IM_SERVER_IP + ":" + IM_SERVER_PORT + "/"
                        + IM_SERVER_NAME + "/msg/add");
        LOGGER.log(Level.FINEST, "Adding message to IM service[{0}]",
                   imServiceURL.toString());
        final HTTPRequest httpRequest =
                new HTTPRequest(imServiceURL,
                                HTTPMethod.POST);
        httpRequest.setPayload(requestJSONObject.toString().getBytes("UTF-8"));
        final Future<HTTPResponse> response =
                URL_FETCH_SVC.fetchAsync(httpRequest);
        //            LOGGER.log(Level.FINEST, "IM response[sc={0}]",
        //                       response.get().getResponseCode());
    }

    /**
     * Notify author author with the specified notification content and QQ
     * number of the receiver.
     *
     * @param content the specified notification content
     * @param articleAuthorQQNum the specified notification receiver QQ number
     * @throws Exception exception
     */
    private void notifyArticleAuthor(final String content,
                                     final String articleAuthorQQNum)
            throws Exception {
        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put("key", KEY);
        requestJSONObject.put(Message.MESSAGE_PROCESSOR, "QQ");
        requestJSONObject.put(Message.MESSAGE_CONTENT, content);
        requestJSONObject.put(Message.MESSAGE_TO_ACCOUNT, articleAuthorQQNum);

        final URL imServiceURL =
                new URL("http://" + IM_SERVER_IP + ":" + IM_SERVER_PORT + "/"
                        + IM_SERVER_NAME + "/msg/add");
        LOGGER.log(Level.FINEST, "Adding message to IM service[{0}]",
                   imServiceURL.toString());
        final HTTPRequest httpRequest =
                new HTTPRequest(imServiceURL,
                                HTTPMethod.POST);
        httpRequest.setPayload(requestJSONObject.toString().getBytes("UTF-8"));
        final Future<HTTPResponse> response =
                URL_FETCH_SVC.fetchAsync(httpRequest);
        //            LOGGER.log(Level.FINEST, "IM response[sc={0}]",
        //                       response.get().getResponseCode());
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
