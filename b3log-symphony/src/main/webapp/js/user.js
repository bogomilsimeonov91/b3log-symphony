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

var User = function (args) {
    $.extend(this, args);
}

$.extend(User.prototype, {
    initStatus: function () {
        $("#userStatus span")[0].innerHTML = Util.readCookie("userName") + "&nbsp;|";
    },

    setUserSettings: function () {
        if (Util.validateForm("tip", [{
            "id": "userName",
            "type": "length",
            "tip": this.labels.nameTooLongLabel
        }, {
            "id": "originalPassword",
            "type": "empty",
            "tip": this.labels.passwordEmptyLabel
        }, {
            "id": "newPassword",
            "type": "empty",
            "tip": this.labels.passwordEmptyLabel
        }])) {
            if ($("#newPassword").val() === $("#confirmPassword").val()) {
                var requestJSONObject = {
                    "sign": $("#sign").val(),
                    "userName": $("#userName").val().replace(/(^\s*)|(\s*$)/g, ""),
                    "userURL": $("#userURL").val(),
                    "userNewPassword": $("#newPassword").val(),
                    "userPassword" : $("#originalPassword").val()
                };
                $.ajax({
                    url: "/user-settings",
                    type: "POST",
                    data: JSON.stringify(requestJSONObject),
                    success: function(result, textStatus){
                        switch (result.sc) {
                            case true:
                                $("#tip").text(result.msg);
                                break;
                            case false:
                            default:
                                $("#tip").text(result.msg);
                                break;
                        }
                    }
                });
            } else {
                $("#tip").text(this.labels.passwordNoMatchLabel);
                $("#confirmPassword").focus();
            }
        }
    }
});