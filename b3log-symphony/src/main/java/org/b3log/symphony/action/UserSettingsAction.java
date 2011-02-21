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
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Transaction;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.b3log.symphony.util.Langs;
import org.b3log.symphony.util.Users;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User settings. user-settings.ftl
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Feb 21, 2011
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

        final JSONObject user = Users.getCurrentUser();
        try {
            LOGGER.log(Level.FINER, "Current logged in user[name={0}]",
                       user.getString(User.USER_NAME));
            final String email = user.getString(User.USER_EMAIL);

            ret.put(User.USER_EMAIL, email);
            ret.put(User.USER_NAME, user.getString(User.USER_NAME));
            ret.put(User.USER_URL, user.getString(User.USER_URL));
            ret.put(Common.USER_THUMBNAIL_URL,
                    user.getString(Common.USER_THUMBNAIL_URL));
            ret.put(Common.USER_QQ_NUM, user.optString(Common.USER_QQ_NUM));

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
            final JSONObject currentUser = Users.getCurrentUser();
            final String currentUserId = currentUser.getString(Keys.OBJECT_ID);

            final JSONObject queryStringJSONObject =
                    getQueryStringJSONObject(request);
            final String action = queryStringJSONObject.optString("action",
                                                                  "basic");
            final JSONObject userToUpdate = new JSONObject(
                    currentUser, JSONObject.getNames(currentUser));

            if ("basic".equals(action)) {
                final String userName =
                        requestJSONObject.optString(User.USER_NAME);
                if (Users.isInvalidUserName(userName)) {
                    ret.put(Keys.STATUS_CODE, false);
                    ret.put(Keys.MSG, Langs.get("nameErrorLabel"));

                    return ret;
                }

                final JSONObject foundUser = userRepository.getByName(userName);
                if (null != foundUser) { // XXX: Concurrent problem onder cluster env
                    final String foundUserId =
                            foundUser.getString(Keys.OBJECT_ID);
                    if (!currentUserId.equals(foundUserId)) {
                        ret.put(Keys.STATUS_CODE, false);
                        ret.put(Keys.MSG, Langs.get("nameDuplicatedLabel"));

                        return ret;
                    }
                }

                final String userQQNum =
                        requestJSONObject.getString(Common.USER_QQ_NUM);
                userToUpdate.put(Common.USER_QQ_NUM, userQQNum);

                userToUpdate.put(User.USER_NAME, userName);
                userRepository.update(currentUserId, userToUpdate);

                transaction.commit();
                ret.put(Keys.STATUS_CODE, true);
            } else if ("advanced".equals(action)) {
                final String url = requestJSONObject.getString(User.USER_URL);
                userToUpdate.put(User.USER_URL, url);
                userToUpdate.put(Common.USER_THUMBNAIL_URL,
                                 requestJSONObject.getString(
                        Common.USER_THUMBNAIL_URL));
                final String sign = requestJSONObject.getString(Common.SIGN);
                userToUpdate.put(Common.SIGN, sign);

                userRepository.update(currentUserId, userToUpdate);

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
