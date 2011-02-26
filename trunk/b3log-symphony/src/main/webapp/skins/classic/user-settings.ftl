<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${titleUser}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta name="robots" content="none"/>
        <link type="text/css" rel="stylesheet" href="/styles/base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/classic/default-index.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/json2.js"></script>
        <script type="text/javascript" src="/js/util.js"></script>
        <script type="text/javascript" src="/js/user.js"></script>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="symphony-content settings">
            <table class="form left" id="userInfoForm" width="319px" cellspacing="10">
                <caption>${basicInfoLabel}</caption>
                <tr>
                    <th width="57px">
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
                        QQ
                    </th>
                    <td>
                        <input id="userQQNum" value="${userQQNum}"/>
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
            <table class="form info" id="userSettingsForm" width="401px" cellspacing="10">
                <caption>
                    ${settingsInfoLabel}
                </caption>
                <tr>
                    <td width="57px"></td>
                    <td id="userImg">
                        <img src="${userThumbnailURL}"
                             class="big-head-img" alt="${userName}" title="${userName}"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${thumbnailURLLabel}
                    </th>
                    <td>
                        <input id="userThumbnailURL" value="${userThumbnailURL}"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${userURLLabel} 
                    </th>
                    <td>
                        <div class="input-label">http://</div>
                        <input style="border-left-width: 0;width: 229px;" id="userURL" value="${userURL}"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${keyOfSolo1Label}
                    </th>
                    <td>
                        <input id="keyOfSolo" value="${keyOfSolo}"/>
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
            <#include "footer.ftl"/>
        </div>
        <script type="text/javascript">
            var user = new User({
                "labels": {
                    "nameErrorLabel": "${nameErrorLabel}",
                    "changeSuccLabel": "${changeSuccLabel}",
                    "setSuccLabel": "${setSuccLabel}",
                    "imgErrorLabel": "${imgErrorLabel}"
                }
            });
            Util.initStatus();
            Util.bindSubmitAction("userInfoForm", "userSettingsForm");
            $("#userURL").focus(function () {
                $(this).prev()[0].className = "input-label-focus";
            }).blur(function () {
                $(this).prev()[0].className = "input-label";
            });
        </script>
    </body>
</html>
