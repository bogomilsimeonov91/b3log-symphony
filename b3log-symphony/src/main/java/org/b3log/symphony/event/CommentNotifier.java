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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.symphony.im.Message;
import org.b3log.symphony.im.qq.QQ;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Comment;
import org.b3log.symphony.repository.CommentRepository;
import org.b3log.symphony.repository.impl.CommentGAERepository;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;

/**
 * This listener is responsible for processing comment reply.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 21, 2011
 */
public final class CommentNotifier
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CommentNotifier.class.getName());
    /**
     * QQ robot 1.
     */
    private static final QQ QQ_ROBOT1;

    static {
        final String qqRobot1Account = Symphonys.get("qqRobot1Account");
        final String qqRobot1Pwd = Symphonys.get("qqRobot1Pwd");
        QQ_ROBOT1 = new QQ(qqRobot1Account, qqRobot1Pwd);
    }

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
            if (!QQ_ROBOT1.isLoggedIn()) {
                QQ_ROBOT1.login();
            }
            
            final String commentContent =
                    comment.getString(Comment.COMMENT_CONTENT);
            final String commentSharpURL =
                    comment.getString(Comment.COMMENT_SHARP_URL);
//            final String articleTitle = article.getString(Article.ARTICLE_TITLE);
//            final String articleLink = "http://" + Symphonys.HOST + article.
//                    getString(Article.ARTICLE_PERMALINK);

            final JSONObject message = new JSONObject();
            message.put(Message.MESSAGE_TO_ACCOUNT, "845765");
            message.put(Message.MESSAGE_CONTENT, commentContent);

            QQ_ROBOT1.send(message);
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
