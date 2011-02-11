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

import java.util.regex.Pattern;

/**
 * User utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 11, 2011
 */
public final class Users {

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
     * Private default constructor.
     */
    private Users() {
    }
}
