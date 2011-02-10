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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Query;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.repository.TagRepository;
import org.b3log.symphony.repository.impl.TagGAERepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Gets tags servlet.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 10, 2011
 */
// TODO: unused
public final class GetTagsServlet extends HttpServlet {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(GetTagsServlet.class.getName());
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagGAERepository.getInstance();

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        final StringBuilder stringBuilder = new StringBuilder("[");
        try {
            final JSONObject result =
                    tagRepository.get(new Query());
            final JSONArray tagArray = result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < tagArray.length(); i++) {
                final JSONObject tag = tagArray.getJSONObject(i);
                final String tagTitle = "\"" + tag.get(Tag.TAG_TITLE) + "\"";
                stringBuilder.append(tagTitle);
                if (i < tagArray.length() -1) {
                    stringBuilder.append(",");
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        stringBuilder.append("]");

        final PrintWriter printWriter = response.getWriter();

        printWriter.write(stringBuilder.toString());
        printWriter.flush();
        printWriter.close();
    }
}
