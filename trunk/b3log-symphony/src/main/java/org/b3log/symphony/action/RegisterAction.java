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
import org.b3log.latke.Latkes;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.MD5;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Register new user.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Feb 7, 2011
 */
public final class RegisterAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(RegisterAction.class.getName());
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
            final String captcha = requestJSONObject.getString("captcha");
            final HttpSession session = request.getSession();
            final String storedCaptcha =
                    (String) session.getAttribute("captcha");

            if (null == storedCaptcha || !storedCaptcha.equals(captcha)) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                ret.put(Keys.STATUS_CODE, "captchaError");
                ret.put(Keys.MSG, langs.get("captchaErrorLabel"));

                return ret;
            }

            final String userName =
                    requestJSONObject.getString(User.USER_NAME);
            final String userEmail =
                    requestJSONObject.getString(User.USER_EMAIL).toLowerCase();
            final String userPwd =
                    requestJSONObject.getString(User.USER_PASSWORD);

            JSONObject user = userRepository.getByEmail(userEmail);
            if (null != user) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                ret.put(Keys.STATUS_CODE, "duplicated");
                ret.put(Keys.MSG, langs.get("emailDuplicatedLabel"));

                return ret;
            }

            user = new JSONObject();
            user.put(User.USER_NAME, userName);
            user.put(User.USER_EMAIL, userEmail);
            user.put(User.USER_PASSWORD, MD5.hash(userPwd));
            user.put(User.USER_URL, " ");
            user.put(Common.STATE, "active");
            user.put(Common.SIGN, " ");

            final String oId = userRepository.add(user);
            user.put(Keys.OBJECT_ID, oId);

            LoginAction.login(user, request);

            transaction.commit();

            ret.put(Keys.STATUS_CODE, "succ");
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                ret.put(Keys.STATUS_CODE, "failed");
            } catch (final JSONException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new ActionException(ex);
            }
        }

        return ret;
    }
}
