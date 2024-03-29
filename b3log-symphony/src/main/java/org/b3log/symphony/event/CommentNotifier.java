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

import java.net.URL;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.model.User;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.urlfetch.HTTPRequest;
import org.b3log.latke.urlfetch.HTTPResponse;
import org.b3log.latke.urlfetch.URLFetchService;
import org.b3log.latke.urlfetch.URLFetchServiceFactory;
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
 * @version 1.0.0.6, Aug 6, 2011
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
     * BR.
     */
    private static final String BR = "\r\n";

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject eventData = event.getData();
        final JSONObject comment = eventData.optJSONObject(Comment.COMMENT);
        final JSONObject article = eventData.optJSONObject(Article.ARTICLE);
        LOGGER.log(Level.FINER,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                eventData, CommentNotifier.class.getName()});
        try {
            final String articleAuthorId =
                    article.getString(Common.AUTHOR_ID);
            final JSONObject articleAuthor =
                    userRepository.get(articleAuthorId);
            final String articleAuthorQQNum =
                    articleAuthor.optString(Common.USER_QQ_NUM);
            boolean needToNotifyArticleAuthor = false;
            boolean needToNotifyOriginalCmter = false;

            final String commenterId =
                    comment.getString(Comment.COMMENTER_ID);
            if (!Strings.isEmptyOrNull(articleAuthorQQNum)
                && !commenterId.equals(articleAuthorId)) {
                needToNotifyArticleAuthor = true;
            }

            String originalCmterQQNum = null;
            final String originalCmterId =
                    comment.optString(Comment.COMMENT_ORIGINAL_CMTER_ID);
            if (!Strings.isEmptyOrNull(originalCmterId)) {
                final JSONObject originalCmter =
                        userRepository.get(originalCmterId);
                if (null != originalCmter) {
                    originalCmterQQNum =
                            originalCmter.optString(Common.USER_QQ_NUM);
                }
            }
            if (!Strings.isEmptyOrNull(originalCmterQQNum)
                && !commenterId.equals(originalCmterId)) {
                needToNotifyOriginalCmter = true;
            }

            if (!needToNotifyArticleAuthor && !needToNotifyOriginalCmter) {
                return;
            }

            final StringBuilder contentBuilder = new StringBuilder();
            String commentContentHTML =
                    comment.getString(Comment.COMMENT_CONTENT);
            commentContentHTML = replaceEmotions(commentContentHTML);
            final String contentText = Jsoup.parse(commentContentHTML).text();
            final String commentSharpURL =
                    comment.getString(Comment.COMMENT_SHARP_URL);

            final JSONObject commenter = userRepository.get(commenterId);
            final String commenterName = commenter.getString(User.USER_NAME);
            contentBuilder.append(article.getString(Article.ARTICLE_TITLE)).
                    append(BR).append("----").append(BR).append(commenterName).
                    append(": ").append(contentText).append(BR).
                    append(commentSharpURL).append("comment");

            if (articleAuthorId.equals(originalCmterId)) {
                needToNotifyArticleAuthor = false;
            }

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
                new HTTPRequest();
        httpRequest.setURL(imServiceURL);
        httpRequest.setRequestMethod(HTTPRequestMethod.POST);

        httpRequest.setPayload(requestJSONObject.toString().getBytes("UTF-8"));
        @SuppressWarnings("unchecked")
        final Future<HTTPResponse> response =
                (Future<HTTPResponse>) URL_FETCH_SVC.fetchAsync(httpRequest);
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
        final HTTPRequest httpRequest = new HTTPRequest();
        httpRequest.setURL(imServiceURL);
        httpRequest.setRequestMethod(HTTPRequestMethod.POST);
        httpRequest.setPayload(requestJSONObject.toString().getBytes("UTF-8"));
        @SuppressWarnings("unchecked")
        final Future<HTTPResponse> response =
                (Future<HTTPResponse>) URL_FETCH_SVC.fetchAsync(httpRequest);
        //            LOGGER.log(Level.FINEST, "IM response[sc={0}]",
        //                       response.get().getResponseCode());
    }

    /**
     * Replaces emotions for escape of QQ.
     *
     * @param commentHTML the original comment HTML
     * @return replaced comment content
     */
    private static String replaceEmotions(final String commentHTML) {
        String ret = commentHTML;
        ret = ret.replace(
                "<img src='/skins/classic/emotions/em00.png' border=0>",
                "/wx ").
                replace("<img src='/skins/classic/emotions/em01.png' border=0>",
                        "/cy ").
                replace("<img src='/skins/classic/emotions/em02.png' border=0>",
                        "/ka ").
                replace("<img src='/skins/classic/emotions/em03.png' border=0>",
                        "/kk ").
                replace("<img src='/skins/classic/emotions/em04.png' border=0>",
                        "/ll ").
                replace("<img src='/skins/classic/emotions/em05.png' border=0>",
                        "/ch ").
                replace("<img src='/skins/classic/emotions/em06.png' border=0>",
                        "/zhem ").
                replace("<img src='/skins/classic/emotions/em07.png' border=0>",
                        "/fn ").
                replace("<img src='/skins/classic/emotions/em08.png' border=0>",
                        "/fd ").
                replace("<img src='/skins/classic/emotions/em09.png' border=0>",
                        "/jy ").
                replace("<img src='/skins/classic/emotions/em10.png' border=0>",
                        "/kuk ").
                replace("<img src='/skins/classic/emotions/em11.png' border=0>",
                        "/tp ").
                replace("<img src='/skins/classic/emotions/em12.png' border=0>",
                        "/xin ").
                replace("<img src='/skins/classic/emotions/em13.png' border=0>",
                        "/xs ").
                replace("<img src='/skins/classic/emotions/em14.png' border=0>",
                        "/huaix ");
        LOGGER.log(Level.FINEST,
                   " Comment content with emotions replaced[{0}]", ret);

        return ret;
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
