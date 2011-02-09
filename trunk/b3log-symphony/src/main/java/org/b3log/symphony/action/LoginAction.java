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

import java.util.HashMap;
import java.util.logging.Level;
import org.b3log.latke.action.ActionException;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.model.User;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Sessions;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.b3log.symphony.util.Langs;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Login.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Feb 7, 2011
 */
public final class LoginAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(LoginAction.class.getName());
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        ret.putAll(Langs.all());

        return ret;
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject requestJSONObject,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        final JSONObject ret = new JSONObject();

        try {
            final String captcha = requestJSONObject.getString("captcha");
            final HttpSession session = request.getSession();
            final String storedCaptcha =
                    (String) session.getAttribute("captcha");

            if (null == storedCaptcha || !storedCaptcha.equals(captcha)) {
                ret.put(Keys.STATUS_CODE, "captchaError");
                ret.put(Keys.MSG, Langs.get(
                        "captchaErrorLabel"));

                return ret;
            }

            final String userEmail =
                    requestJSONObject.getString(User.USER_EMAIL).toLowerCase();
            final String userPwd =
                    requestJSONObject.getString(User.USER_PASSWORD);

            final JSONObject user = userRepository.getByEmail(userEmail);
            if (null != user) {
                final String requestPwdHash = MD5.hash(userPwd);
                if (user.getString(User.USER_PASSWORD).equals(requestPwdHash)) {
                    login(user, request);
                    ret.put(Keys.STATUS_CODE, true);
                    ret.put(User.USER_EMAIL, userEmail);
                    ret.put(User.USER_NAME, user.getString(User.USER_NAME));
                    ret.put(User.USER_URL, user.getString(User.USER_URL));

                    return ret;
                }
            }

            ret.put(Keys.STATUS_CODE, false);
            ret.put(Keys.MSG, Langs.get(
                    "userNotFoundOrPwdErrorLabel"));

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG, "Internal Error!");
            } catch (final JSONException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new ActionException(ex);
            }
        }

        return ret;
    }

    /**
     * Logins the specified user for the specified request.
     *
     * @param user the specified user
     * @param request the specified request
     * @throws Exception exception
     */
    static void login(final JSONObject user, final HttpServletRequest request)
            throws Exception {
        final String userEmail = user.getString(User.USER_EMAIL);
        final String userPwd = user.getString(User.USER_PASSWORD);
        final String requestPwdHash = MD5.hash(userPwd);
        final HttpSession session = request.getSession();

        Sessions.login(request, userEmail, requestPwdHash);
        session.setAttribute(
                "userId", user.getString(Keys.OBJECT_ID));
        session.setAttribute(User.USER_NAME,
                             user.getString(User.USER_NAME));
        session.setAttribute(User.USER_EMAIL,
                             user.getString(User.USER_EMAIL));
        session.setAttribute(User.USER_URL,
                             user.getString(User.USER_URL));
    }
}
