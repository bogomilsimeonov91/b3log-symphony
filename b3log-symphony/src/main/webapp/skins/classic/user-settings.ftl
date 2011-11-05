<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${titleUser} - ${titleIndex}"></@head>
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
                        <input id="userName" value="${userName}" type="text" />
                    </td>
                </tr>
                <tr>
                    <th>
                        QQ
                    </th>
                    <td>
                        <input id="userQQNum" value="${userQQNum}" type="text" />
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
                        <input id="userThumbnailURL" value="${userThumbnailURL}" type="text" />
                    </td>
                </tr>
                <tr>
                    <th>
                        ${userURLLabel} 
                    </th>
                    <td>
                        <div class="input-label">http://</div>
                        <input type="text" style="border-left-width: 0;width: 229px;" id="userURL" value="${userURL}" />
                    </td>
                </tr>
                <tr>
                    <th>
                        ${keyOfSolo1Label}
                    </th>
                    <td>
                        <input id="keyOfSolo" value="${keyOfSolo}" type="text" />
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
