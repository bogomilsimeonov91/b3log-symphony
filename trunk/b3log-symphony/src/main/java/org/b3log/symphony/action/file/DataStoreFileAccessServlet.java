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

package org.b3log.symphony.action.file;

import com.google.appengine.api.datastore.Blob;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Ids;
import org.b3log.latke.util.Strings;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.File;
import org.b3log.symphony.repository.FileRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.repository.impl.FileGAERepository;
import org.b3log.symphony.repository.impl.UserGAERepository;
import org.json.JSONObject;

/**
 * File access via
 * <a href="http://code.google.com/appengine/docs/java/javadoc/com/google/appengine/api/datastore/package-summary.html">
 * Google Data Store Low-level API</a>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Feb 9, 2011
 */
public final class DataStoreFileAccessServlet extends HttpServlet {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(DataStoreFileAccessServlet.class.getName());
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * File repository.
     */
    private FileRepository fileRepository =
            FileGAERepository.getInstance();
    /**
     * Maximum file size(200K).
     */
    private static final long MAX_SIZE = 1024 * 200;
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();
    /**
     * Language service.
     */
    private static final LangPropsService LANG_PROP_SVC =
            LangPropsService.getInstance();
    /**
     * Languages.
     */
    private static Map<String, String> langs = null;

    static {
        try {
            langs = LANG_PROP_SVC.getAll(Latkes.getDefaultLocale());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        JSONObject oldUser = null;
        try {
            final HttpSession session = request.getSession();
            if (null == session) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);

                return;
            }

            final String email = (String) session.getAttribute(User.USER_EMAIL);
            if (null == email) {
                LOGGER.log(Level.WARNING, "Not logged in[email={0}]", email);
                response.sendError(HttpServletResponse.SC_FORBIDDEN);

                return;
            }

            oldUser = userRepository.getByEmail(email);
            if (null == oldUser) {
                LOGGER.log(Level.WARNING, "Not found user[email={0}]", email);
                response.sendError(HttpServletResponse.SC_FORBIDDEN);

                return;
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        final ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iterator = null;

        try {
            iterator = upload.getItemIterator(request);

            while (iterator.hasNext()) {
                final FileItemStream item = iterator.next();
                final InputStream stream = item.openStream();

                if (item.isFormField()) {
                    continue;
                }

                String contentType = item.getContentType();
                if (Strings.isEmptyOrNull(contentType)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);

                    return;
                }

                contentType = contentType.trim().toLowerCase();
                if (!"image/jpeg".equals(contentType)
                    && !"image/gif ".equals(contentType)
                    && !"image/png".equals(contentType)) {
                    LOGGER.log(Level.WARNING, "Thumbnail[contentType={0}]",
                               contentType);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);

                    return;
                }

                // XXX: check size before streaming
                final byte[] contentBytes = IOUtils.toByteArray(stream);
                if (contentBytes.length > MAX_SIZE) {
                    final String cause =
                            langs.get("exceedMaxUploadSizeLabel");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                       cause);

                    return;
                }

                if (0 == contentBytes.length) {
                    final String cause = langs.get("fileEmptyLabel");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                       cause);
                    return;
                }

                final Blob blob = new Blob(contentBytes);
                final JSONObject file = new JSONObject();
                final String id = Ids.genTimeMillisId();
                file.put(Keys.OBJECT_ID, id);

                file.put(File.FILE_CONTENT_TYPE, contentType);
                file.put(File.FILE_CONTENT, blob);

                final String fileName = oldUser.getString(User.USER_EMAIL)
                                        + "/thumbnail";
                file.put(File.FILE_NAME, fileName);
                final long fileSize = contentBytes.length;
                file.put(File.FILE_SIZE, fileSize);

                final String thumbnailFileId =
                        oldUser.optString(Common.USER_THUMBNAIL_FILE_ID);
                if (!Strings.isEmptyOrNull(thumbnailFileId)) {
                    fileRepository.remove(thumbnailFileId);
                }

                final JSONObject userToUpdate = new JSONObject(
                        oldUser, JSONObject.getNames(oldUser));
                final String userId = oldUser.getString(Keys.OBJECT_ID);
                userToUpdate.put(Common.USER_THUMBNAIL_FILE_ID, id);
                userRepository.update(userId, userToUpdate);

                fileRepository.add(file);
            }

            response.sendRedirect("/user-settings");
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException("File upload error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        final String id = request.getParameter(Keys.OBJECT_ID);
        try {
            final JSONObject file = fileRepository.get(id);
            if (null == file) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final Blob content = (Blob) file.get(File.FILE_CONTENT);
            final String name = file.getString(File.FILE_NAME);
            String charset = "ISO-8859-1";
            if (request.getLocale().getLanguage().equals("zh")) {
                charset = "GBK";
            }
            response.addHeader("Content-Disposition",
                               "attachment; filename="
                               + new String(name.getBytes(charset), "ISO-8859-1"));
            response.setContentType(file.getString(File.FILE_CONTENT_TYPE));
            response.getOutputStream().write(content.getBytes());
            response.getOutputStream().close();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException("File download error: " + e.getMessage());
        }
    }
}
