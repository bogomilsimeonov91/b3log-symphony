<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${titleUser}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="robots" content="none"/>
        <link type="text/css" rel="stylesheet" href="/styles/base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/classic/default-index.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/json2.js"></script>
        <script type="text/javascript" src="/js/util.js"></script>
        <script type="text/javascript" src="/js/user.js"></script>
    </head>
    <body>
        <#include "user-top.ftl"/>
        <div class="header">
            <#include "user-header.ftl"/>
        </div>
        <div class="content">
            <table class="form" id="userInfoForm">
                <caption>${basicInfoLabel}</caption>
                <tr>
                    <th>
                        ${emailLabel}
                    </th>
                    <td>
                        ${userEmail}
                    </td>
                </tr>
                <tr>
                    <th>
                        ${userNameLabel}
                    </th>
                    <td>
                        <input id="userName" value="${userName}"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${originalPasswordLabel}
                    </th>
                    <td>
                        <input type="password" id="originalPassword"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${newPasswordLabel}
                    </th>
                    <td>
                        <input type="password" id="newPassword"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${confirmPasswordLabel}
                    </th>
                    <td>
                        <input type="password" id="confirmPassword"/>
                    </td>
                </tr>
                <tr>
                    <th colspan="2">
                        <span class="red" id="tipUserInfo"></span>
                        <button onclick="user.setUserInfo();">
                            ${submitLabel}
                        </button>
                    </th>
                </tr>
            </table>
            <form action="/file" method="POST" enctype="multipart/form-data">
                <table cellpadding="0" cellspacing="9">
                    <caption>
                        ${headSettingLabel}
                    </caption>
                    <tr>
                        <td colspan="2">
                            <img src="${userThumbnailURL}" alt="${userName}" title="${userName}"/>
                        </td>
                    </tr>
                    <tr>
                        <td id="uploadFile">
                            <input type='file' name='myFile'/>
                        </td>
                        <td>
                            <button type="submit">${submitLabel}</button>
                        </td>
                    </tr>
                </table>
            </form>
            <table class="form" id="userSettingsForm">
                <caption>
                    ${settingsInfoLabel}
                </caption>
                <tr>
                    <th>
                        ${userURLLabel} http://
                    </th>
                    <td>
                        <input id="userURL" value="${userURL}"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${signLabel}
                    </th>
                    <td>
                        <textarea id="sign">${sign}</textarea>
                    </td>
                </tr>
                <tr>
                    <th colspan="2">
                        <span class="red" id="tip"></span>
                        <button onclick="user.setUserSettings();">
                            ${submitLabel}
                        </button>
                    </th>
                </tr>
            </table>
        </div>
        <div class="footer">
            <#include "user-footer.ftl"/>
        </div>
        <script type="text/javascript">
            var user = new User({
                "labels": {
                    "passwordEmptyLabel": "${passwordEmptyLabel}",
                    "passwordNoMatchLabel": "${passwordNoMatchLabel}",
                    "nameTooLongLabel": "${nameTooLongLabel}",
                    "changeSuccLabel": "${changeSuccLabel}"
                }
            });
            user.initStatus();
            Util.bindSubmitAction("userInfoForm", "userSettingsForm");
        </script>
    </body>
</html>
