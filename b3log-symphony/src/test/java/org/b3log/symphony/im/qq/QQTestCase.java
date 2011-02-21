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

package org.b3log.symphony.im.qq;

import org.b3log.symphony.util.Symphonys;
import org.b3log.symphony.im.Message;
import org.b3log.latke.Keys;
import org.json.JSONObject;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * {@link QQ} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 21, 2011
 */
public final class QQTestCase {

    /**
     * QQ.
     */
    private QQ qq = new QQ(Symphonys.get("qqRobot1Account"),
                           Symphonys.get("qqRobot1Pwd"));

    /**
     * Tests methods {@linkplain QQ#login()} and
     * {@linkplain QQ#send(org.json.JSONObject)}.
     * 
     * @throws Exception exception
     */
    @Test
    public void login() throws Exception {
        final JSONObject loginResult = qq.login();
        assertTrue(loginResult.getBoolean(Keys.STATUS_CODE));

        final JSONObject message = new JSONObject();
        message.put(Message.MESSAGE_TO_ACCOUNT, "845765");
        message.put(Message.MESSAGE_CONTENT, "Test!");

        final JSONObject sendResult = qq.send(message);

    }
}
