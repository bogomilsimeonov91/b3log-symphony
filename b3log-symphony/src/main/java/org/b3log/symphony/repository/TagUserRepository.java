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

package org.b3log.symphony.repository;

import java.util.List;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.RepositoryException;
import org.json.JSONObject;

/**
 * Tag-User repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 31, 2011
 */
public interface TagUserRepository extends Repository {

    /**
     * Gets tag-user relations by the specified user id.
     *
     * @param userId the specified user id
     * @return for example
     * <pre>
     * [{
     *         "oId": "",
     *         "tag_oId": "",
     *         "user_oId": articleId
     * }, ....], returns an empty list if not found
     * </pre>
     * @throws RepositoryException repository exception
     */
    List<JSONObject> getByUserId(final String userId)
            throws RepositoryException;

    /**
     * Gets tag-user relations by the specified tag id.
     *
     * @param tagId the specified tag id
     * @param currentPageNum the specified current page number, MUST greater
     * then {@code 0}
     * @param pageSize the specified page size(count of a page contains objects),
     * MUST greater then {@code 0}
     * @return for example
     * <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 88250
     *     },
     *     "rslts": [{
     *         "oId": "",
     *         "tag_oId": tagId,
     *         "user_oId": ""
     *     }, ....]
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    JSONObject getByTagId(final String tagId,
                          final int currentPageNum,
                          final int pageSize) throws RepositoryException;

    /**
     * Gets a tag-user relation by the specified tag id and user id.
     *
     * @param tagId the specified tag id
     * @param userId the specified user id
     * @return a tag-user relation, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    JSONObject getByTagIdAndUserId(final String tagId,
                                   final String userId)
            throws RepositoryException;

    /**
     * Gets the top tag users with the specified tag id and fetch size.
     *
     * @param tagId the specified tag id
     * @param fetchSize the specified fetch size
     * @return a list of user ids, return an empty list if not found
     */
    List<String> getTopTagUsers(final String tagId, final int fetchSize);
}
