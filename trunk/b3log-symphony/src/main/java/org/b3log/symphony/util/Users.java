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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.b3log.latke.model.User;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.impl.UserGAERepository;
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
    private static final UserService USER_SVC =
            UserServiceFactory.getUserService();

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
     * @return the current user, {@code null} if not found
     */
    public static JSONObject getCurrentUser() {
        final com.google.appengine.api.users.User currentUser =
                USER_SVC.getCurrentUser();
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
    public static JSONObject registerUser(final String userEmail) throws Exception {
        final JSONObject ret = new JSONObject();

        ret.put(User.USER_EMAIL, userEmail);
        ret.put(User.USER_NAME, UserGAERepository.genTimeMillisId());
        ret.put(User.USER_URL, "");
        ret.put(Common.STATE, Common.AVAILABLE);
        ret.put(Common.SIGN, "");

        USER_REPOSITORY.addAsync(ret);

        return ret;
    }

    /**
     * Private default constructor.
     */
    private Users() {
    }
}
