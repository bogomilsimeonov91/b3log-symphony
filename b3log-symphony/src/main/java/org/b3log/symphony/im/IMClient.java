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

import org.json.JSONObject;

/**
 * Instant messenger.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 21, 2011
 */
public interface IMClient {

    /**
     * Sets login account with the specified login account.
     *
     * @param loginAccount the specified login account
     */
    void setLoginAccount(final String loginAccount);

    /**
     * Gets login account.
     *
     * @return login account
     */
    String getLoginAccount();

    /**
     * Set login password with the specified login password.
     * 
     * @param loginPwd the specified login password
     */
    void setLoginPwd(final String loginPwd);

    /**
     * Gets login password.
     *
     * @return login password
     */
    String getLoginPwd();

    /**
     * Logins this instant messenger.
     *
     * @return login result, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     */
    JSONObject login();

    /**
     * Sends the specified message.
     *
     * @param message the specified message, for example,
     * <pre>
     * {
     *     "messageContent": ""
     * }
     * </pre>
     * @return send result, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     */
    JSONObject send(final JSONObject message);

    /**
     * Determines whether this client is logged in.
     *
     * @return {@code true} if it is logged in, {@code false} otherwise
     */
    boolean isLoggedIn();
}
