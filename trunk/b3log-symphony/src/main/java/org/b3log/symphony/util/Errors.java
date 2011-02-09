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

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.symphony.model.ErrorPage;

/**
 * Error utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 9, 2011
 */
public final class Errors {

    /**
     * Sends error via {@linkplain HttpServletResponse#sendError(int, java.lang.String)}
     * with the specified error URI and cause.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @param errorCode the specified error code
     * @param errorURI the specified error URI
     * @param cause the specified cause
     * @throws IOException io exception
     */
    public static void sendError(final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final int errorCode,
                                 final String errorURI,
                                 final String cause) throws IOException {
        request.setAttribute(ErrorPage.ERROR_PAGE_REQUEST_URI,
                             errorURI);
        request.setAttribute(ErrorPage.ERROR_PAGE_CAUSE, cause);
        response.sendError(errorCode, cause);
    }

    /**
     * Private default constructor.
     */
    private Errors() {
    }
}
