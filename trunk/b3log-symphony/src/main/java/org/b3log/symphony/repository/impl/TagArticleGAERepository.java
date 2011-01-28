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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.repository.TagArticleRepository;
import org.json.JSONObject;

/**
 * Tag-Article relation Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 28, 2011
 */
public final class TagArticleGAERepository extends AbstractGAERepository
        implements TagArticleRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagArticleGAERepository.class.getName());

    @Override
    public String getName() {
        return Tag.TAG + "_" + Article.ARTICLE;
    }

    @Override
    public List<JSONObject> getByArticleId(final String articleId)
            throws RepositoryException {
        final Query query = new Query(getName());
        query.addFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                        Query.FilterOperator.EQUAL, articleId);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final Iterable<Entity> entities = preparedQuery.asIterable();

        final List<JSONObject> ret = new ArrayList<JSONObject>();
        for (final Entity entity : entities) {
            final Map<String, Object> properties = entity.getProperties();
            final JSONObject e = new JSONObject(properties);

            ret.add(e);
        }

        return ret;
    }

    /**
     * Gets the {@link TagArticleGAERepository} singleton.
     *
     * @return the singleton
     */
    public static TagArticleGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private TagArticleGAERepository() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 23, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final TagArticleGAERepository SINGLETON =
                new TagArticleGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
