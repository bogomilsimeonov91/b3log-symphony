<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${titleRegister}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta name="author" content="B3log Team"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta name="revised" content="B3log, 2011"/>
        <meta http-equiv="Window-target" content="_top"/>
        <link type="text/css" rel="stylesheet" href="/styles/base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/classic/default-index.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/json2.js"></script>
        <script type="text/javascript" src="/js/util.js"></script>
        <script type="text/javascript" src="/js/index.js"></script>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="header">
            <#include "header.ftl"/>
        </div>
        <div class="content">
            <div class="left">
                <table id="loginForm" width="320px" class="form">
                    <caption>${loginLabel}</caption>
                    <tr>
                        <th>
                            ${emailLabel}
                        </th>
                        <td>
                            <input id="emailLogin"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${passwordLabel}
                        </th>
                        <td>
                            <input id="passwordLogin" type="password"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${captchaLabel}
                        </th>
                        <td>
                            <input id="captchaLogin" class="normal-input"/>
                            <img alt="captcha" src="/captcha"></img>
                        </td>
                    </tr>
                    <tr>
                        <th colspan="2">
                            <span class="red" id="tipLogin"></span>
                            <button onclick="index.login();">
                                ${loginLabel}
                            </button>
                        </th>
                    </tr>
                </table>
            </div>
            <div class="right">
                <table id="registerForm" width="320px" class="form">
                    <caption>${registerLabel}</caption>
                    <tr>
                        <th>
                            ${userNameLabel}
                        </th>
                        <td>
                            <input id="userName"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${emailLabel}
                        </th>
                        <td>
                            <input id="email"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${passwordLabel}
                        </th>
                        <td>
                            <input id="password" type="password"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${confirmPasswordLabel}
                        </th>
                        <td>
                            <input id="confirmPassword" type="password"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${captchaLabel}
                        </th>
                        <td>
                            <input id="captcha" class="normal-input"/>
                            <img alt="captcha" src="/captcha"></img>
                        </td>
                    </tr>
                    <tr>
                        <th colspan="2">
                            <span class="red" id="tip"></span>
                            <button onclick="index.register();">
                                ${registerLabel}
                            </button>
                        </th>
                    </tr>
                </table>
            </div>
            <div class="clear"></div>
        </div>
        <div class="footer">
            <#include "footer.ftl"/>
        </div>
        <script type="text/javascript">
            var index = new Index({
                "labels": {
                    "emailErrorLabel": "${emailErrorLabel}",
                    "passwordEmptyLabel": "${passwordEmptyLabel}",
                    "passwordNoMatchLabel": "${passwordNoMatchLabel}",
                    "captchaCannotEmptyLabel": "${captchaCannotEmptyLabel}",
                    "nameTooLongLabel": "${nameTooLongLabel}"
                }
            });

            Util.bindSubmitAction("loginForm", "registerForm");
            $("#emailLogin").val(Util.readCookie("userEmail"));
        </script>
    </body>
</html>
