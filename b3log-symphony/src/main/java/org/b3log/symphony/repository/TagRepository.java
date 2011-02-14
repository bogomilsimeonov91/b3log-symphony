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
 * Tag repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Feb 14, 2011
 */
public interface TagRepository extends Repository {

    /**
     * Gets a tag by the specified tag title ignored case.
     *
     * @param tagTitle the specified tag title
     * @return a tag, {@code null} if not found
     * @throws RepositoryException repository exception
     */
    JSONObject getByTitle(final String tagTitle)
            throws RepositoryException;

    /**
     * Gets most used tags with the specified number.
     *
     * @param num the specified number
     * @return a list of most used tags, returns an empty list if not found
     */
    List<JSONObject> getMostUsedTags(final int num);

    /**
     * Gets recent articles of the specified tag title and fetch size.
     *
     * @param tagTitle the specified tag title
     * @param fetchSize the specified fetch size
     * @return a list of articles, returns an empty list if not fount
     * @throws RepositoryException repository exception
     */
    List<JSONObject> getRecentArticles(final String tagTitle,
                                       final int fetchSize)
            throws RepositoryException;

    /**
     * Gets top comment articles of the specified tag title and fetch size.
     *
     * @param tagTitle the specified tag title
     * @param fetchSize the specified fetch size
     * @return a list of articles, returns an empty list if not fount
     * @throws RepositoryException repository exception
     */
    List<JSONObject> getTopArticles(final String tagTitle,
                                    final int fetchSize)
            throws RepositoryException;

    /**
     * Gets articles with the specified tag title, current page number and page
     * size.
     *
     * @param tagTitle the specified tag title
     * @param currentPageNum the specified page number
     * @param pageSize the specified page size
     * @return a list of articles, returns an empty list if not fount
     * @throws RepositoryException repository exception
     */
    List<JSONObject> getArticles(final String tagTitle,
                                 final int currentPageNum, final int pageSize)
            throws RepositoryException;
}
