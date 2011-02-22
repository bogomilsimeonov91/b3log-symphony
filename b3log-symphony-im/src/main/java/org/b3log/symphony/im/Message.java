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

package org.b3log.symphony.im;

/**
 * This class defines all instant message model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 21, 2011
 */
public final class Message {

    /**
     * Message.
     */
    public static final String MESSAGE = "message";
    /**
     * Messages.
     */
    public static final String MESSAGES = "messages";
    /**
     * Key of message content.
     */
    public static final String MESSAGE_CONTENT = "messageContent";
    /**
     * Key of message to account.
     */
    public static final String MESSAGE_TO_ACCOUNT = "messageToAccount";
    /**
     * Key of message processor.
     */
    public static final String MESSAGE_PROCESSOR = "messageProcessor";

    /**
     * Private default constructor.
     */
    private Message() {
    }
}
