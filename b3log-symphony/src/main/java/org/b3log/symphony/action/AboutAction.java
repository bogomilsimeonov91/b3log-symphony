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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.symphony.action.util.Filler;
import org.b3log.symphony.util.Langs;
import org.json.JSONObject;

/**
 * About action. about.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Aug 6, 2011
 */
public final class AboutAction extends AbstractCacheablePageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AboutAction.class.getName());

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();
        ret.putAll(Langs.all());

        Filler.fillCommon(ret);

        request.setAttribute(AbstractCacheablePageAction.CACHED_LINK,
                             "Unspecified");
        request.setAttribute(AbstractCacheablePageAction.CACHED_OID,
                             "Unspecified");
        request.setAttribute(AbstractCacheablePageAction.CACHED_TITLE,
                             "Unspecified");
        request.setAttribute(AbstractCacheablePageAction.CACHED_TYPE,
                             "Unspecified");

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
