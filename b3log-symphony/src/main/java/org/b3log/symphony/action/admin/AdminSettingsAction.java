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
package org.b3log.symphony.action.admin;

import java.util.HashMap;
import java.util.logging.Level;
import javax.servlet.http.HttpSession;
import org.b3log.latke.action.ActionException;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Strings;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.b3log.symphony.util.Errors;
import org.b3log.symphony.util.Langs;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Admin settings. admin-settings.ftl
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 6, 2011
 */
public final class AdminSettingsAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AdminSettingsAction.class.getName());
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        ret.putAll(Langs.all());

//        try {
//
//        } catch (final Exception e) {
//            LOGGER.log(Level.SEVERE, e.getMessage(), e);
//        }

        return ret;
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject requestJSONObject,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        final JSONObject ret = new JSONObject();

        final Transaction transaction = userRepository.beginTransaction();

        try {
            final HttpSession session = request.getSession();
            if (null == session) {
                final String cause = Langs.get(
                        "loginFirstLabel");
                Errors.sendError(request, response,
                                 HttpServletResponse.SC_FORBIDDEN,
                                 request.getRequestURI(), cause);

                return ret;
            }

            final String email = (String) session.getAttribute(User.USER_EMAIL);
            if (null == email) {
                final String cause = Langs.get(
                        "loginFirstLabel");
                Errors.sendError(request, response,
                                 HttpServletResponse.SC_FORBIDDEN,
                                 request.getRequestURI(), cause);

                return ret;
            }

            final JSONObject oldUser = userRepository.getByEmail(email);
            if (null == oldUser) {
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG,
                        Langs.get(
                        "userNotFoundOrPwdErrorLabel"));
            }

            final String userId = oldUser.getString(Keys.OBJECT_ID);

            String action = request.getParameter("action");
            if (Strings.isEmptyOrNull(action)) {
                action = "basic";
            }
            
            final JSONObject userToUpdate = new JSONObject(
                    oldUser, JSONObject.getNames(oldUser));

            if ("basic".equals(action)) {
                String pwdHash =
                        MD5.hash(requestJSONObject.getString(User.USER_PASSWORD));
                if (!oldUser.getString(User.USER_PASSWORD).equals(pwdHash)) {
                    ret.put(Keys.STATUS_CODE, false);
                    ret.put(Keys.MSG,
                            Langs.get("oldPwdErrorLabel"));

                    return ret;
                }

                final String userName =
                        requestJSONObject.getString(User.USER_NAME);
                pwdHash = MD5.hash(
                        requestJSONObject.getString(User.USER_NEW_PASSWORD));

                userToUpdate.put(User.USER_NAME, userName);
                userToUpdate.put(User.USER_PASSWORD, pwdHash);
                userRepository.update(userId, userToUpdate);

                transaction.commit();
                ret.put(Keys.STATUS_CODE, true);
            } else if ("advanced".equals(action)) {
                final String url = requestJSONObject.getString(User.USER_URL);
                final String sign = requestJSONObject.getString(Common.SIGN);

                userToUpdate.put(User.USER_URL, url);
                userToUpdate.put(Common.SIGN, sign);

                userRepository.update(userId, userToUpdate);

                transaction.commit();
                ret.put(Keys.STATUS_CODE, true);
            } else {
                transaction.rollback();
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG, "Unknown action!");
            }
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
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
}
