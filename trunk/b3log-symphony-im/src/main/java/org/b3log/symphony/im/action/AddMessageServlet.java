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

package org.b3log.symphony.im.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.symphony.im.Message;
import org.b3log.symphony.im.qq.QQ;
import org.b3log.symphony.im.util.Strings;
import org.b3log.symphony.im.util.Symphonys;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Add message..
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 22, 2011
 */
public final class AddMessageServlet extends HttpServlet {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AddMessageServlet.class.getName());
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * QQ robot 1.
     */
    private static final QQ QQ_ROBOT1;

    static {
        QQ_ROBOT1 = new QQ(Symphonys.get("qqRobot1Account"),
                           Symphonys.get("qqRobot1Pwd"));
        QQ_ROBOT1.login();
    }

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        JSONObject data = null;
        try {
            final String jsonString = toJSONString(request);
            data = new JSONObject(jsonString);

            final String key = data.getString("key");
            if (!Symphonys.KEY.equals(key)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);

                return;
            }

            final String msgProcessor =
                    data.getString(Message.MESSAGE_PROCESSOR);
            if (!"QQ".equals(msgProcessor)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);

                return;
            }

            final String msgContent = data.getString(Message.MESSAGE_CONTENT);
            final String msgToAccount =
                    data.getString(Message.MESSAGE_TO_ACCOUNT);

            if (!QQ_ROBOT1.isLoggedIn()) {
                QQ_ROBOT1.login();
            }

            LOGGER.log(Level.FINER, "Message[content={0}]", msgContent);
            final JSONObject message = new JSONObject();
            message.put(Message.MESSAGE_CONTENT, msgContent);
            message.put(Message.MESSAGE_TO_ACCOUNT, msgToAccount);

            QQ_ROBOT1.send(message);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Converts the specified http servlet request to a json string.
     *
     * @param request the specified http servlet request
     * @return a json string if the specified http servlet request could convert,
     *         otherwise, returns "{}"
     * @throws IOException io exception
     * @throws JSONException json exception
     */
    private String toJSONString(final HttpServletRequest request)
            throws IOException, JSONException {
        request.setCharacterEncoding("UTF-8");
        
        final StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = request.getReader();
        } catch (final IllegalStateException e) {
            reader = new BufferedReader(new InputStreamReader(
                    request.getInputStream()));
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return "{}";
        }

        String line = reader.readLine();
        while (null != line) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();

        String tmp = sb.toString();
        if (Strings.isEmptyOrNull(tmp)) {
            tmp = "{}";
        }

        return tmp;
    }
}
