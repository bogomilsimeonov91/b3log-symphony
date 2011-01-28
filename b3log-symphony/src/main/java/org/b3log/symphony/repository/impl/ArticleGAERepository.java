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

import java.util.logging.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.RunsOnEnv;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.repository.ArticleRepository;

/**
 * Article Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 28, 2011
 */
public final class ArticleGAERepository extends AbstractGAERepository
        implements ArticleRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleGAERepository.class.getName());
    /**
     * Cache.
     */
    private static final Cache<String, Object> CACHE;

    static {
        final RunsOnEnv runsOnEnv = Latkes.getRunsOnEnv();
        if (!runsOnEnv.equals(RunsOnEnv.GAE)) {
            throw new RuntimeException(
                    "GAE repository can only runs on Google App Engine, please "
                    + "check your configuration and make sure "
                    + "Latkes.setRunsOnEnv(RunsOnEnv.GAE) was invoked before "
                    + "using GAE repository.");
        }

        CACHE = CacheFactory.getCache(
                ArticleGAERepository.class.getSimpleName() + "Cache");
    }

    @Override
    public String getName() {
        return Article.ARTICLE;
    }

    /**
     * Gets the {@link ArticleGAERepository} singleton.
     *
     * @return the singleton
     */
    public static ArticleGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private ArticleGAERepository() {
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
        private static final ArticleGAERepository SINGLETON =
                new ArticleGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
