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

package org.b3log.symphony.im.qq;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.symphony.im.IMClient;
import org.b3log.symphony.im.Keys;
import org.b3log.symphony.im.Message;
import org.b3log.symphony.im.qq.util.HTTPClient;
import org.b3log.symphony.im.util.Strings;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * QQ.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Feb 22, 2011
 */
public final class QQ implements IMClient {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(QQ.class.getName());
    /**
     * Login account(QQ number here).
     */
    private String loginAccount;
    /**
     * Login password.
     */
    private String loginPwd;
    /**
     * Login URL.
     */
    private static final URL LOGIN_URL;
    /**
     * SID.
     */
    private String sid;
    /**
     * Server number.
     */
    private String serverNumber;
    /**
     * HTTP client.
     */
    private HTTPClient httpClient = new HTTPClient();
    /**
     * Logged in?
     */
    private boolean loggedIn;

    /**
     * Constructs a QQ with the specified login account and login password.
     *
     * @param loginAccount the specified login account
     * @param loginPwd the specified login password
     */
    public QQ(final String loginAccount, final String loginPwd) {
        setLoginAccount(loginAccount);
        setLoginPwd(loginPwd);
    }

    static {
        try {
            LOGIN_URL = new URL("http://pt.3g.qq.com/handleLogin");
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Loin URL.
     */
    public static final URL NLOGIN_URL;

    static {
        try {
            NLOGIN_URL = new URL("http://pt.3g.qq.com/s?aid=nLogin3gqq");
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets SID from 3GQQ Server.
     *
     * @return SID, returns {@code null} if failed
     */
    public String getSID() {
        final String result = httpClient.get(NLOGIN_URL);

        LOGGER.log(Level.FINE, "QQ sid result[{0}]", result);

        if (Strings.isEmptyOrNull(result)
            || midString(result, "sid=", "==").equals("")) {
            return null;
        }

        return midString(result, "sid=", "==") + "==";
    }

    /**
     * Gets the middle string start with the specified start string, end with
     * the specified end string in the specified source string.
     *
     * @param source the specified source string
     * @param start the specified start string
     * @param end the specified end string
     * @return middle string, returns an empty string if not found
     */
    public static String midString(final String source,
                                   final String start,
                                   final String end) {
        final int apos = source.indexOf(start);
        final int bpos = source.indexOf(end, apos + start.length());

        if (apos == -1 || bpos == -1) {
            return "";
        } else {
            return source.substring(apos + start.length(), bpos);
        }
    }

    @Override
    public JSONObject login() {
        LOGGER.log(Level.INFO, "Login QQ robot[qqNumber={0}]", loginAccount);
        final JSONObject ret = new JSONObject();
        try {
            sid = getSID();
            final String result =
                    httpClient.post(LOGIN_URL,
                                    "sid=" + sid
                                    + "&qq=" + loginAccount + "&pwd="
                                    + loginPwd
                                    + "&bid_code=3GQQ&modifySKey=0&toQQchat=true&loginType=1&aid=nLoginHandle");

            if (-1 != result.indexOf("错误，请输入正确的QQ号码")) {
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG, "Wrong login account");

                return ret;
            }

            if (-1 != result.indexOf("登录密码错误")
                && -1 != result.indexOf("字母大小写")) {
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG, "Wrong login password");

                return ret;
            }

            final String serverNum = midString(result, "ontimer=\"http://",
                                               ".3g.qq.com");
            if (serverNum.equals("")) {
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG, "Not found server");

                return ret;
            }

            serverNumber = serverNum;
            sid = midString(result, "?aid=nqqchatMain&amp;sid=", "&amp;");

            LOGGER.log(Level.INFO, "Logged in QQ[qqNumber={0}, loginResult={1}]",
                       new Object[]{getLoginAccount(), result});

            ret.put(Keys.STATUS_CODE, true);

            loggedIn = true;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG, e.getMessage());
            } catch (final JSONException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), e);

                throw new RuntimeException(ex);
            }
        }

        return ret;
    }

    @Override
    public JSONObject send(final JSONObject message) {
        final JSONObject ret = new JSONObject();

        String result = null;
        String toAccount = null;
        String messageContent = null;
        try {
            final URL sendURL = new URL("http://" + serverNumber
                                        + ".3g.qq.com/g/s?sid=" + sid
                                        + "&aid=sendmsg&tfor=qq");
            toAccount = message.getString(Message.MESSAGE_TO_ACCOUNT);
            messageContent = message.getString(Message.MESSAGE_CONTENT);
            result = httpClient.post(
                    sendURL, "msg=" + URLEncoder.encode(messageContent, "UTF-8")
                             + "&u=" + toAccount + "&saveURL=0&do=send&on=1");

            if (-1 != result.indexOf("发送成功")) {
                LOGGER.log(Level.INFO,
                           "Sent message to QQ[qqNumber={0}, content={1}] succeeded",
                           new Object[]{toAccount, messageContent});

                ret.put(Keys.STATUS_CODE, true);
            } else {
                LOGGER.log(Level.INFO,
                           "Sent message to QQ[qqNumber={0}, content={1}] failed",
                           new Object[]{toAccount, messageContent});
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG, "Unknown reason");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG, "Send error[msg=" + e.getMessage() + "]");
            } catch (final JSONException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        }

        return ret;
    }

    @Override
    public void setLoginAccount(final String loginAccount) {
        this.loginAccount = loginAccount;
    }

    @Override
    public String getLoginAccount() {
        return loginAccount;
    }

    @Override
    public void setLoginPwd(final String loginPwd) {
        this.loginPwd = loginPwd;
    }

    @Override
    public String getLoginPwd() {
        return loginPwd;
    }

    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }
}
