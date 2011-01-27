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
    </head>
    <body>
        <#include "common-top.ftl"/>
        <div class="header">
            <#include "header.ftl"/>
        </div>
        <div class="content">
            <table width="320px" class="form">
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
                        ${passwordLabel}
                    </th>
                    <td>
                        <input id="password"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${confirmPasswordLabel}
                    </th>
                    <td>
                        <input id="confirmPassword"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${captchaLabel}
                    </th>
                    <td>
                        <input id="captcha" class="normal-input"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span class="red" id="tip">s</span>
                    </td>
                    <th colspan="2">
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
                var userName = $("#userName").val().replace(/(^\s*)|(\s*$)/g, ""),
                password = $("#password").val().replace(/(^\s*)|(\s*$)/g, "");
                if (userName === "" || userName.length > 21) {
                    $("tip").text("${nameTooLongLabel}");
                    $("#userName").focus();
                } else if (password === "") {
                    $("tip").text("${passwordEmptyLabel}");
                    $("#password").focus();
                } else if (password === $("#confirmPassword").val()) {
                    $("tip").text("${passwordNoMatchLabel}");
                    $("#password").focus();
                } else if ($("#captcha").val().replace(/(^\s*)|(\s*$)/g, "") === "") {
                    $("tip").text("${captchaCannotEmptyLabel}");
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
