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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.b3log.latke.Keys;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.MD5;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.json.JSONObject;

/**
 * Registers new user.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 27, 2011
 */
public final class RegisterServlet extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(RegisterServlet.class.getName());
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        final JSONObject ret = new JSONObject();

        final PrintWriter writer = response.getWriter();
        final Transaction transaction = userRepository.beginTransaction();
        try {
            final String captcha = request.getParameter("captcha");
            final HttpSession session = request.getSession();
            final String storedCaptcha =
                    (String) session.getAttribute("captcha");

            if (null == storedCaptcha || !storedCaptcha.equals(captcha)) {
                ret.put(Keys.STATUS_CODE, "captchaError");
                writer.write(ret.toString());

                return;
            }

            final String userEmail = request.getParameter(User.USER_EMAIL);
            final String userPwd = request.getParameter(User.USER_PASSWORD);

            JSONObject user = userRepository.getByEmail(userEmail);
            if (null != user) {
                ret.put(Keys.STATUS_CODE, "duplicated");

                return;
            }

            user = new JSONObject();
            user.put(User.USER_EMAIL, userEmail);
            user.put(User.USER_PASSWORD, MD5.hash(userPwd));

            userRepository.add(user);
            transaction.commit();

            ret.put(Keys.STATUS_CODE, "succeed");

        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            writer.write(ret.toString());
            writer.flush();
            writer.close();
        }

        response.sendRedirect("/");
    }
}
