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

package org.b3log.symphony.im.qq.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.util.Strings;

/**
 * A very simple HTTP client for instant messenger.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 21, 2011
 */
public final class HTTPClient {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(HTTPClient.class.getName());
    /**
     * Cookie.
     */
    private String cookie = "";

    /**
     * Gets the string for the specified URL by HTTP GET method.
     *
     * @param url the specified URL
     * @return result string, returns {@code null} if failed
     */
    public String get(final URL url) {
        HttpURLConnection httpConn = null;

        try {
            final int timeout = 30000;
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(timeout);
            httpConn.setReadTimeout(timeout);
            httpConn.setRequestMethod("GET");
            httpConn.setRequestProperty("Host", url.getHost());
            httpConn.setRequestProperty("User-Agent",
                                        "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
            httpConn.setRequestProperty("Accept",
                                        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpConn.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.5");
            httpConn.setRequestProperty("Accept-Encoding", "gzip,deflate");
            httpConn.setRequestProperty("Accept-Charset",
                                        "UTF-8;q=0.7,*;q=0.7");
            if (!getCookie().equals("")) {
                httpConn.setRequestProperty("Cookie", "" + getCookie() + ";");
            }

            httpConn.setRequestProperty("Keep-Alive", "300");
            httpConn.setRequestProperty("Connection", "keep-alive");
            httpConn.setRequestProperty("Cache-Control", "no-cache");

            final Map<String, List<String>> headerFields = httpConn.
                    getHeaderFields();
            final String cookieString = getCookieString(headerFields);

            if (!Strings.isEmptyOrNull(cookieString)) {
                setCookie(cookieString.substring(0, cookieString.indexOf(";")));
            }

            final InputStream inputStream = httpConn.getInputStream();
            final BufferedReader br =
                    new BufferedReader(new InputStreamReader(inputStream));
            String line = br.readLine();
            final StringBuilder contentBuilder = new StringBuilder(line);
            while (null != line) {
                contentBuilder.append(line);
                line = br.readLine();
            }

            LOGGER.finer(new String(contentBuilder.toString().getBytes(),
                                    "GB2312"));

            return new String(contentBuilder.toString().getBytes(), "UTF-8");
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        } finally {
            httpConn.disconnect();
        }
    }

    /**
     * Gets the bytes for the specified URL by HTTP GET method.
     *
     * @param url the specified URL
     * @return result bytes, returns {@code null} if failed
     */
    public byte[] getBin(final URL url) {
        byte[] ret = null;
        HttpURLConnection httpConn = null;

        try {
            final int timeout = 30000;
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(timeout);
            httpConn.setReadTimeout(timeout);
            httpConn.setRequestMethod("GET");
            httpConn.setRequestProperty("Host", url.getHost());
            httpConn.setRequestProperty("User-Agent",
                                        "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
            httpConn.setRequestProperty("Accept",
                                        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpConn.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.5");
            httpConn.setRequestProperty("Accept-Encoding", "gzip,deflate");
            httpConn.setRequestProperty("Accept-Charset",
                                        "UTF-8;q=0.7,*;q=0.7");
            if (!(getCookie().equals(""))) {
                httpConn.setRequestProperty("Cookie", "" + getCookie() + ";");
            }
            httpConn.setRequestProperty("Keep-Alive", "300");
            httpConn.setRequestProperty("Connection", "keep-alive");
            httpConn.setRequestProperty("Cache-Control", "no-cache");
            ret = new byte[httpConn.getContentLength()];

            final String cookieString =
                    getCookieString(httpConn.getHeaderFields());
            if (!Strings.isEmptyOrNull(cookieString)) {
                setCookie(cookieString.substring(0, cookieString.indexOf(";")));
            }

            final InputStream inputStream = httpConn.getInputStream();
            inputStream.read(ret);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        } finally {
            httpConn.disconnect();
        }
    }

    /**
     * Posts to the specified URL with the specified para string.
     *
     * <p>
     *   <b>Note</b>: The para string MUST BE URL encoded.
     * </p>
     *
     * @param url the specified URL
     * @param parmString the specified para string
     * @return result string, returns {@code null} if failed
     */
    public String post(final URL url, final String parmString) {
        HttpURLConnection httpConn = null;

        try {
            final int timeout = 30000;
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(timeout);
            httpConn.setReadTimeout(timeout);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Host", url.getHost());
            httpConn.setRequestProperty("User-Agent",
                                        "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
            httpConn.setRequestProperty("Accept",
                                        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpConn.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.5");
            httpConn.setRequestProperty("Accept-Encoding", "gzip,deflate");
            httpConn.setRequestProperty("Accept-Charset",
                                        "UTF-8;q=0.7,*;q=0.7");
            if (!(getCookie().equals(""))) {
                httpConn.setRequestProperty("Cookie", "" + getCookie() + ";");
            }

            httpConn.setRequestProperty("Keep-Alive", "300");
            httpConn.setRequestProperty("Connection", "keep-alive");
            httpConn.setRequestProperty("Cache-Control", "no-cache");
            httpConn.setRequestProperty("Content-Type",
                                        "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("Content-Length",
                                        String.valueOf(parmString.length()));
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            final OutputStreamWriter out =
                    new OutputStreamWriter(httpConn.getOutputStream());
            out.write(parmString);
            out.close();

            final String cookieString =
                    getCookieString(httpConn.getHeaderFields());
            if (!Strings.isEmptyOrNull(cookieString)) {
                setCookie(cookieString.substring(0, cookieString.indexOf(";")));
            }

            final InputStream inputStream = httpConn.getInputStream();
            final BufferedReader br =
                    new BufferedReader(new InputStreamReader(inputStream));
            String line = br.readLine();
            final StringBuilder contentBuilder = new StringBuilder(line);
            while (null != line) {
                contentBuilder.append(line);
                line = br.readLine();
            }

            return new String(contentBuilder.toString().getBytes(), "UTF-8");
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        } finally {
            httpConn.disconnect();
        }
    }

    /**
     * Sets cookie with the specified cookie.
     * 
     * @param cookie the specified cookie
     */
    public void setCookie(final String cookie) {
        this.cookie = cookie;
    }

    /**
     * Gets cookie.
     *
     * @return cookie string
     */
    public String getCookie() {
        return cookie;
    }

    /**
     * Clears cookie.
     */
    public void clearCookie() {
        this.cookie = "";
    }

    /**
     * Gest "set-cookie" header string from the specified header fields.
     *
     * @param headerFields the specified header fields
     * @return "set-cookie" header string, returns {@code null} if not found
     */
    private String getCookieString(final Map<String, List<String>> headerFields) {
        String ret = null;
        for (final Map.Entry<String, List<String>> entry 
                : headerFields.entrySet()) {
            if (null != entry.getKey()) {
                final String headerName = entry.getKey().toLowerCase();
                if ("set-cookie".equals(headerName)) {
                    ret = entry.getValue().get(0);
                }
            }
        }

        return ret;
    }
}
