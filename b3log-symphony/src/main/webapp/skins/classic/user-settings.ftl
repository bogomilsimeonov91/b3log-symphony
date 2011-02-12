<!DOCTYPE html>
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
        <div class="content settings">
            <div class="left info">
                <table class="form" id="userInfoForm" width="300px">
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
                        <th colspan="2">
                            <span class="tip" id="tipUserInfo"></span>
                            <button onclick="user.setUserInfo();">
                                ${submitLabel}
                            </button>
                        </th>
                    </tr>
                </table>
            </div>
            <table class="form right" id="userSettingsForm" width="379px">
                <caption>
                    ${settingsInfoLabel}
                </caption>
                 <tr>
                    <th width="96px;">
                        ${thumbnailURLLabel}
                    </th>
                    <td>
                        <input id="userThumbnailURL" value="${userThumbnailURL}"/>
                    </td>
                </tr>
                <tr>
                    <th width="96px;">
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
                        <span class="tip" id="tip"></span>
                        <button onclick="user.setUserSettings();">
                            ${submitLabel}
                        </button>
                    </th>
                </tr>
            </table>
            <div class="clear"></div>
        </div>
        <div class="footer">
            <#include "user-footer.ftl"/>
        </div>
        <iframe name="hideFrame" class="none" id="hideFrame"></iframe>
        <script type="text/javascript">
            var user = new User({
                "labels": {
                    "nameErrorLabel": "${nameErrorLabel}",
                    "changeSuccLabel": "${changeSuccLabel}",
                    "setSuccLabel": "${setSuccLabel}"
                }
            });
            user.initStatus();
            user.setHead();
            Util.bindSubmitAction("userInfoForm", "userSettingsForm");
        </script>
    </body>
</html>
