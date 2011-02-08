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
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Sessions;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User settings. user-settings.ftl
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Feb 8, 2011
 */
public final class UserSettingsAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(UserSettingsAction.class.getName());
    /**
     * Language service.
     */
    private static final LangPropsService LANG_PROP_SVC =
            LangPropsService.getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();
    /**
     * Languages.
     */
    private static Map<String, String> langs = null;

    static {
        try {
            langs = LANG_PROP_SVC.getAll(
                    Latkes.getDefaultLocale());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {

        final Map<String, Object> ret = new HashMap<String, Object>();

        ret.putAll(langs);
        try {
            final String email = Sessions.currentUserName(request);
            if (null == email) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);

                return ret;
            }

            final JSONObject user = userRepository.getByEmail(email);

            ret.put(User.USER_EMAIL, email);
            ret.put(User.USER_NAME, user.getString(User.USER_NAME));
            ret.put(User.USER_URL, user.getString(User.USER_URL));
            ret.put(Common.SIGN, user.getString(Common.SIGN));
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

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
            final String email = Sessions.currentUserName(request);
            if (null == email) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);

                return ret;
            }

            final JSONObject oldUser = userRepository.getByEmail(email);
            String pwdHash =
                    MD5.hash(requestJSONObject.getString(User.USER_PASSWORD));
            if (null == oldUser
                || !oldUser.getString(User.USER_PASSWORD).equals(pwdHash)) {
                ret.put(Keys.STATUS_CODE, HttpServletResponse.SC_FORBIDDEN);

                return ret;
            }

            final String userName = requestJSONObject.getString(User.USER_NAME);
            pwdHash = MD5.hash(
                    requestJSONObject.getString(User.USER_NEW_PASSWORD));
            final String url = requestJSONObject.getString(User.USER_URL);
            final String sign = requestJSONObject.getString(Common.SIGN);
            final String userId = oldUser.getString(Keys.OBJECT_ID);

            final JSONObject userToUpdate = new JSONObject();
            userToUpdate.put(User.USER_NAME, userName);
            userToUpdate.put(User.USER_EMAIL, email);
            userToUpdate.put(User.USER_PASSWORD, pwdHash);
            userToUpdate.put(User.USER_URL, url);
            userToUpdate.put(Common.SIGN, sign);

            userRepository.update(userId, userToUpdate);

            transaction.commit();
            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                ret.put(Keys.STATUS_CODE, false);
            } catch (final JSONException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new ActionException(ex);
            }
        }

        return ret;
    }
}
