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
package org.b3log.symphony.repository.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.symphony.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Feb 11, 2011
 */
public final class UserGAERepository extends AbstractGAERepository
        implements UserRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(UserGAERepository.class.getName());

    @Override
    public String getName() {
        return User.USER;
    }

    @Override
    public void update(final String id, final JSONObject userToUpdate)
            throws RepositoryException {
        super.updateAsync(id, userToUpdate);

        // TODO: ugly, resolve it in Latke repository component
        final String name = userToUpdate.optString(User.USER_NAME);
        Query query = new Query().addFilter(User.USER_NAME,
                                            FilterOperator.EQUAL,
                                            name);
        String cacheKey = CACHE_KEY_PREFIX + query.hashCode() + "_" + getName();
        CACHE.remove(cacheKey);
        final String email = userToUpdate.optString(User.USER_EMAIL);
        query = new Query().addFilter(User.USER_EMAIL,
                                      FilterOperator.EQUAL,
                                      email.toLowerCase());
        cacheKey = CACHE_KEY_PREFIX + query.hashCode() + "_" + getName();
        CACHE.remove(cacheKey);
    }

    @Override
    public JSONObject getByName(final String name) throws RepositoryException {
        final Query query = new Query().addFilter(User.USER_NAME,
                                                  FilterOperator.EQUAL,
                                                  name);
        final JSONArray result = get(query).optJSONArray(Keys.RESULTS);

        return result.optJSONObject(0);
    }

    @Override
    public JSONObject getByEmail(final String email) throws RepositoryException {
        final Query query = new Query().addFilter(User.USER_EMAIL,
                                                  FilterOperator.EQUAL,
                                                  email.toLowerCase());
        final JSONArray result = get(query).optJSONArray(Keys.RESULTS);

        return result.optJSONObject(0);
    }

    @Override
    public boolean isAdminEmail(final String email)
            throws RepositoryException {
        final JSONObject user = getByEmail(email);

        if (null == user) {
            return false;
        }

        try {
            return Role.ADMIN_ROLE.equals(user.getString(User.USER_ROLE));
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            throw new RepositoryException(e);
        }
    }

    /**
     * Gets the {@link UserGAERepository} singleton.
     *
     * @return the singleton
     */
    public static UserGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private UserGAERepository() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final UserGAERepository SINGLETON =
                new UserGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
