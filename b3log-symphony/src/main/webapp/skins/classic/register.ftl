<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${titleLabel}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta name="author" content="B3log Team"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta name="revised" content="B3log, 2011"/>
        <meta http-equiv="Window-target" content="_top"/>
        <link type="text/css" rel="stylesheet" href="/styles/base.css"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-index.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
    </head>
    <body>
        <#include "common-top.ftl"/>
        <div class="header">
            <#include "header.ftl"/>
        </div>
        <div class="content">
            <table width="320px" class="form">
                <caption>${registerLabel}</caption>
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
                        <button onclick="register();">
                            ${registerLabel}
                        </button>
                    </th>
                </tr>
            </table>
        </div>
        <div class="footer">
            <#include "footer.ftl"/>
        </div>
        <script type="text/javascript">
            function register() {
                var email = $("#email").val().replace(/(^\s*)|(\s*$)/g, ""),
                password = $("#password").val().replace(/(^\s*)|(\s*$)/g, "");
                if (email === "" || !/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(email)) {
                    $("#tip").text("${emailErrorLabel}");
                    $("#email").focus();
                } else if (password === "") {
                    $("#tip").text("${passwordEmptyLabel}");
                    $("#password").focus();
                } else if (password !== $("#confirmPassword").val()) {
                    $("#tip").text("${passwordNoMatchLabel}");
                    $("#password").focus();
                } else if ($("#captcha").val().replace(/(^\s*)|(\s*$)/g, "") === "") {
                    $("#tip").text("${captchaCannotEmptyLabel}");
                    $("#captcha").focus();
                } else {
                    $.ajax({
                        url: "/register",
                        type: "POST",
                        success: function(result, textStatus){
                            
                        }
                    });
                }
            }
        </script>
    </body>
</html>
