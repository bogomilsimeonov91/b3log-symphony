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
package org.b3log.symphony.filter;

import java.util.logging.Logger;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Level;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.util.Strings;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;

/**
 * Page cache filter.
 *
 * <p>
 * All request URI ends with ".ftl" will be redirected to "/".
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Nov 21, 2011
 * @see org.b3log.latke.action.AbstractCacheablePageAction#afterDoFreeMarkerTemplateAction(
 * javax.servlet.http.HttpServletRequest,
 * javax.servlet.http.HttpServletResponse,
 * java.util.Map, freemarker.template.Template)
 * @see #shouldSkip(java.lang.String) 
 */
public final class PageCacheFilter implements Filter {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageCacheFilter.class.getName());
    /**
     * Enables page cache?
     */
    private static final boolean PAGE_CACHE_ENABLED =
            Boolean.valueOf(Symphonys.get("enablePageCache"));

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Writes response page content(cached/generated).
     *
     * @param request the specified request
     * @param response the specified response
     * @param chain filter chain
     * @throws IOException io exception
     * @throws ServletException servlet exception
     */
    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain) throws IOException,
                                                         ServletException {
        final long startTimeMillis = System.currentTimeMillis();
        final HttpServletRequest httpServletRequest =
                (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse =
                (HttpServletResponse) response;

        final String requestURI = httpServletRequest.getRequestURI();
        if (!"/get-news".equals(requestURI)) {
            // XXX: Redicts to http://b3log-solo.googlecode.com temp
            httpServletResponse.sendRedirect("http://b3log-solo.googlecode.com");
            return;
        }

        if (requestURI.endsWith(".ftl")) {
            httpServletResponse.sendRedirect("/");

            return;
        }

        final String queryString = httpServletRequest.getQueryString();

        String pageCacheKey = requestURI;
        if (!Strings.isEmptyOrNull(queryString)) {
            pageCacheKey += "?" + queryString;
        }

        final Cache<String, Object> cache = PageCaches.getCache();
        LOGGER.log(Level.FINER, "Request[pageCacheKey={0}]", pageCacheKey);
        LOGGER.log(Level.FINEST, "Page cache[cachedCount={0}, maxCount={1}]",
                   new Object[]{cache.getCachedCount(), cache.getMaxCount()});
        JSONObject cachedPageContentObject =
                (JSONObject) cache.get(pageCacheKey);
        if (!PAGE_CACHE_ENABLED) {
            cachedPageContentObject = null;
            LOGGER.log(Level.FINEST, "Page cache is disabled");
        }

        if (null == cachedPageContentObject) {
            httpServletRequest.setAttribute(Keys.PAGE_CACHE_KEY,
                                            pageCacheKey);
            chain.doFilter(request, response);

            final long endimeMillis = System.currentTimeMillis();
            final String dateString = Article.DATE_FORMAT.format(
                    new Date(endimeMillis));
            final PrintWriter writer = getResponseWriter(httpServletResponse);
            final String msg = String.format(
                    "<!-- Generated by B3log Symphony(%1$d ms), %2$s -->",
                    endimeMillis - startTimeMillis, dateString);
            LOGGER.finer(msg);
            writer.append(msg);
            writer.flush();
            writer.close();
        } else {
            try {
                LOGGER.log(Level.FINEST,
                           "Writes resposne for page[pageCacheKey={0}] from cache",
                           pageCacheKey);
                response.setContentType("text/html");
                response.setCharacterEncoding("UTF-8");
                final PrintWriter writer = response.getWriter();
                String cachedPageContent =
                        cachedPageContentObject.getString(
                        AbstractCacheablePageAction.CACHED_CONTENT);
                final String cachedType = cachedPageContentObject.optString(
                        AbstractCacheablePageAction.CACHED_TYPE);
                if (!Strings.isEmptyOrNull(cachedType)) {
                    final String oId = cachedPageContentObject.getString(
                            AbstractCacheablePageAction.CACHED_OID);
                    LOGGER.log(Level.FINEST,
                               "Cached value[key={0}, oId={1}, type={2}]",
                               new Object[]{pageCacheKey, oId, cachedType});
                    if (Strings.isEmptyOrNull(oId)) {
                        httpServletResponse.sendError(
                                HttpServletResponse.SC_NOT_FOUND);

                        return;
                    }
                }

                final long endimeMillis = System.currentTimeMillis();
                final String dateString = Article.DATE_FORMAT.format(
                        new Date(endimeMillis));
                final String msg = String.format(
                        "<!-- Cached by B3log Symphony(%1$d ms), %2$s -->",
                        endimeMillis - startTimeMillis, dateString);
                LOGGER.finer(msg);
                cachedPageContent += msg;
                writer.write(cachedPageContent);
                writer.flush();
                writer.close();
            } catch (final Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * Gets the writer of the specified http servlet response.
     * 
     * @param httpServletResponse the specified http servlet response
     * @return writer
     * @throws IOException io exception
     */
    private PrintWriter getResponseWriter(
            final HttpServletResponse httpServletResponse) throws IOException {
        PrintWriter ret = null;

        try {
            ret = httpServletResponse.getWriter();
        } catch (final IllegalStateException e) {
            ret = new PrintWriter(httpServletResponse.getOutputStream());
        }

        return ret;
    }
}
