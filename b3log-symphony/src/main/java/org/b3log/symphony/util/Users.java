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
package org.b3log.symphony.util;

import com.dlog4j.util.UBBDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.user.GeneralUser;
import org.b3log.latke.user.UserService;
import org.b3log.latke.user.gae.GAEUserService;
import org.b3log.latke.util.Ids;
import org.b3log.symphony.action.EntryAction;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Feb 12, 2011
 */
public final class Users {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Users.class.getName());
    /**
     * User repository.
     */
    private static final UserGAERepository USER_REPOSITORY =
            UserGAERepository.getInstance();
    /**
     * User service.
     */
    private static final UserService USER_SVC = new GAEUserService();

    /**
     * Gets the specified user's sign as HTML content.
     *
     * @param user the specified user
     * @return sign HTML
     * @throws JSONException json exception
     */
    public static String getUserSignHTML(final JSONObject user)
            throws JSONException {
        String sign = user.getString(Common.SIGN);
        sign = sign.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        sign = UBBDecoder.decode(sign);

        return sign;
    }

    /**
     * Checks whether the specified email is invalid.
     *
     * @param email the specified email
     * @return {@code true} if the specified data is invalid, {@code false}
     * otherwise
     */
    public static boolean isInvalidEmail(final String email) {
        final Pattern pattern =
                Pattern.compile(
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        return !pattern.matcher(email).matches();
    }

    /**
     * Checks whether the specified user name is invalid.
     *
     * @param userName the specified user name
     * @return {@code true} if the specified data is invalid, {@code false}
     * otherwise
     */
    public static boolean isInvalidUserName(final String userName) {
        final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]{2,20}$");

        return !pattern.matcher(userName).matches();
    }

    /**
     * Gets the current user.
     *
     * @param request the specified request
     * @return the current user, {@code null} if not found
     */
    public static JSONObject getCurrentUser(
            final HttpServletRequest request) {
        final GeneralUser currentUser = USER_SVC.getCurrentUser(request);
        if (null == currentUser) {
            return null;
        }

        final String email = currentUser.getEmail();
        try {
            JSONObject ret = USER_REPOSITORY.getByEmail(email);
            if (null == ret) {
                ret = registerUser(email);
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return null;
        }
    }

    /**
     * Registers a new user with the specified email.
     *
     * @param userEmail the specified email
     * @return the registered new user
     * @throws Exception exception
     */
    public static JSONObject registerUser(final String userEmail)
            throws Exception {
        final JSONObject ret = new JSONObject();

        ret.put(User.USER_EMAIL, userEmail.toLowerCase());
        ret.put(User.USER_NAME, Ids.genTimeMillisId());
        ret.put(User.USER_URL, "");
        ret.put(Common.STATE, Common.AVAILABLE);
        ret.put(Common.SIGN, "");
        ret.put(Common.USER_THUMBNAIL_URL,
                EntryAction.DEFAULT_USER_THUMBNAIL_URL);

        final Transaction transaction = USER_REPOSITORY.beginTransaction();
        try {
            USER_REPOSITORY.add(ret);

            transaction.commit();
        } catch (final RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Register user failed", e);
        }


        return ret;
    }

    /**
     * Private default constructor.
     */
    private Users() {
    }
}
