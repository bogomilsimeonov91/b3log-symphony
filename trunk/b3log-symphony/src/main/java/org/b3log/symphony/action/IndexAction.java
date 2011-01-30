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

import org.b3log.latke.action.ActionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Latkes;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.service.LangPropsService;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.repository.TagRepository;
import org.b3log.symphony.repository.impl.TagGAERepository;
import org.json.JSONObject;

/**
 * Index action. index.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 23, 2011
 */
public final class IndexAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(IndexAction.class.getName());
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagGAERepository.getInstance();

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            final JSONObject queryStringJSONObject =
                    getQueryStringJSONObject(request);
            final int currentPageNum = queryStringJSONObject.optInt(
                    Pagination.PAGINATION_CURRENT_PAGE_NUM, 1);

            final Locale locale = Latkes.getDefaultLocale();
            final Map<String, String> langs = langPropsService.getAll(locale);
            ret.putAll(langs);

            // Tags
            final int maxTagCnt = 10;
            final int maxArticleCnt = 3;
            final List<JSONObject> tags =
                    tagRepository.getMostUsedTags(maxTagCnt);
            List<JSONObject> articles = null;
            for (int i = 0; i < tags.size(); i++) {
                final JSONObject tag = tags.get(i);
                final String tagTitle = tag.getString(Tag.TAG_TITLE);

                articles = tagRepository.getRecentArticles(tagTitle,
                                                           maxArticleCnt);
                tag.put(Tag.TAG_ARTICLES_REF, (Object) articles);
            }

            ret.put(Tag.TAGS, tags);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }

        return ret;
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");





    }
}