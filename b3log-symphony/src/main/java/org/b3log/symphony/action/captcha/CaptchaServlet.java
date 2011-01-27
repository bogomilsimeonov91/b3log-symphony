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

package org.b3log.symphony.action.captcha;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.symphony.SymphonyServletListener;

/**
 * Captcha servlet..
 *
 * <p>
 *   See <a href="http://isend-blog.appspot.com/2010/03/25/captcha_on_GAE.html">
 *  在GAE上拼接生成图形验证码</a> for philosophy. Checkout
 *    <a href="http://toy-code.googlecode.com/svn/trunk/CaptchaGenerator">
 *    the sample captcha generator</a> for mor details.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 27, 2011
 */
public final class CaptchaServlet extends HttpServlet {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CaptchaServlet.class.getName());
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Length of captcha.
     */
    private static final int LENGTH = 4;
    /**
     * Images service.
     */
    private static final ImagesService IMAGE_SERVICE =
            ImagesServiceFactory.getImagesService();
    /**
     * Random.
     */
    private static final Random RANDOM = new Random();
    /**
     * Key of captcha.
     */
    public static final String CAPTCHA = "captcha";
    /**
     * Maximum captcha row.
     */
    public static final int MAX_CAPTCHA_ROW = 10;
    /**
     * Maximum captcha column.
     */
    public static final int MAX_CAPTCHA_COLUM = 10;
    /**
     * Width of a captcha character.
     */
    public static final int WIDTH_CAPTCHA_CHAR = 13;
    /**
     * Height of a captcha character.
     */
    public static final int HEIGHT_CAPTCHA_CHAR = 20;
     /**
     * Captcha &lt;"imageName", Image&gt;.
     * For example &lt;"0/5.png", Image&gt;.
     */
    public static final Map<String, Image> CAPTCHAS =
            new HashMap<String, Image>();

    static {
        LOGGER.info("Loading captcha images....");
        try {
            final URL captchaURL =
                    SymphonyServletListener.class.getClassLoader().getResource(
                    "captcha.zip");
            final ZipFile zipFile = new ZipFile(captchaURL.getFile());
            final Set<String> imageNames = new HashSet<String>();
            for (int row = 0; row < MAX_CAPTCHA_ROW; row++) {
                for (int column = 0; column < MAX_CAPTCHA_COLUM; column++) {
                    imageNames.add(row + "/" + column + ".png");
                }

            }

            final Iterator<String> i = imageNames.iterator();
            while (i.hasNext()) {
                final String imageName = i.next();
                final ZipEntry zipEntry = zipFile.getEntry(imageName);

                final BufferedInputStream bufferedInputStream =
                        new BufferedInputStream(zipFile.getInputStream(zipEntry));
                final byte[] captchaCharData = new byte[bufferedInputStream.
                        available()];
                bufferedInputStream.read(captchaCharData);
                bufferedInputStream.close();

                final Image captchaChar =
                        ImagesServiceFactory.makeImage(captchaCharData);

                CAPTCHAS.put(imageName, captchaChar);
            }

            zipFile.close();
        } catch (final Exception e) {
            LOGGER.severe("Can not load captchs!");

            throw new RuntimeException(e);
        }

        LOGGER.info("Loaded captch images");
    }

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("image/jpeg");

        final String row = String.valueOf(RANDOM.nextInt(MAX_CAPTCHA_ROW));
        String captcha = "";
        final List<Composite> composites = new ArrayList<Composite>();
        for (int i = 0; i < LENGTH; i++) {
            final String column = String.valueOf(RANDOM.nextInt(
                    MAX_CAPTCHA_COLUM));
            captcha += column;
            final String imageName = row + "/" + column + ".png";
            final Image captchaChar = CAPTCHAS.get(imageName);
            final Composite composite = ImagesServiceFactory.makeComposite(
                    captchaChar, i * WIDTH_CAPTCHA_CHAR, 0,
                    1.0F, Composite.Anchor.TOP_LEFT);
            composites.add(composite);
        }

        final HttpSession httpSession = request.getSession();
        LOGGER.log(Level.FINER, "Captcha[{0}] for session[id={1}]",
                   new Object[]{captcha,
                                httpSession.getId()});
        httpSession.setAttribute(CAPTCHA, captcha);

        final Image captchaImage =
                IMAGE_SERVICE.composite(composites,
                                        WIDTH_CAPTCHA_CHAR * LENGTH,
                                        HEIGHT_CAPTCHA_CHAR,
                                        0);

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        final OutputStream outputStream = response.getOutputStream();
        outputStream.write(captchaImage.getImageData());
        outputStream.close();
    }
}
