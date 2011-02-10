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

var Index = function (args) {
    $.extend(this, args);
}

$.extend(Index.prototype, {
    initStatus: function () {
        var labels = this.labels;
        $.ajax({
            url: "/check-login",
            type: "POST",
            success: function(result, textStatus){
                switch(result.sc) {
                    case true:
                        $("#userStatus").html("<span class='left'>" + result.userName + " |</span>"
                            + "<span title='" + labels.adminConsoleLabel
                            + "' onclick=\"window.location='/user-entries'\" class='admin-icon'></span>"
                            + "<span class='left'>&nbsp;|</span>"
                            + "<span title='" + labels.logoutLabel
                            + "' onclick=\"Util.logout();\" class='logout-icon'></span>");
                        $("#commentForm").show();
                        break;
                    case false:
                        $("#userStatus").html("<span title='" + labels.loginLabel
                            + "' onclick=\"window.location='/register'\" class='login-icon'></span>");
                        break;
                }
            }
        });
    },

    bindSubmitAction: function () {
        for (var i = 0; i < arguments.length; i++) {
            $("#" + arguments[i] + ".form input").keypress(function (event) {
                if (event.keyCode === 13) {
                    $(this).parents(".form").find("button").click();
                }
            });
        }
    },

    register: function () {
        if (Util.validateForm("tip", [{
            "id": "userName",
            "type": "length",
            "tip": this.labels.nameTooLongLabel
        }, {
            "id": "email",
            "type": "email",
            "tip": this.labels.emailErrorLabel
        }, {
            "id": "password",
            "type": "empty",
            "tip": this.labels.passwordEmptyLabel
        }, {
            "id": "captcha",
            "type": "empty",
            "tip": this.labels.captchaCannotEmptyLabel
        }])){
            if ($("#password").val() !== $("#confirmPassword").val()) {
                $("#tip").text(this.labels.passwordNoMatchLabel);
                $("#confirmPassword").focus();
            } else {
                var userName =  $("#userName").val().replace(/(^\s*)|(\s*$)/g, ""),
                email = $("#email").val().replace(/(^\s*)|(\s*$)/g, "");
                var requestJSONObject = {
                    "captcha": $("#captcha").val().replace(/(^\s*)|(\s*$)/g, ""),
                    "userName": userName,
                    "userEmail": email,
                    "userPassword": $("#password").val()
                };

                $.ajax({
                    url: "/register",
                    type: "POST",
                    data: JSON.stringify(requestJSONObject),
                    success: function(result, textStatus){
                        switch(result.sc) {
                            case true:
                                Util.createCookie("userName", userName, 365);
                                Util.createCookie("userEmail", email, 365);
                                Util.createCookie("userURL", "", 365);
                                window.location.href = '/user-settings';
                                break;
                            case "duplicated":
                                $("#tip").text(result.msg);
                                break;
                            case false:
                                $("#tip").text(result.msg);
                                break;
                            case "captchaError":
                                $("#tip").text(result.msg);
                                break;
                        }
                    }
                });
            }
        }
    },

    login: function () {
        if (Util.validateForm("tipLogin", [{
            "id": "emailLogin",
            "type": "email",
            "tip": this.labels.emailErrorLabel
        }, {
            "id": "passwordLogin",
            "type": "empty",
            "tip": this.labels.passwordEmptyLabel
        }, {
            "id": "captchaLogin",
            "type": "empty",
            "tip": this.labels.captchaCannotEmptyLabel
        }])) {
            var email = $("#emailLogin").val().replace(/(^\s*)|(\s*$)/g, "");
            var requestJSONObject = {
                "captcha": $("#captchaLogin").val(),
                "userEmail": email,
                "userPassword": $("#passwordLogin").val()
            };
            $.ajax({
                url: "/login",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    switch(result.sc) {
                        case true:
                            Util.createCookie("userName", result.userName, 365);
                            Util.createCookie("userEmail", email, 365);
                            Util.createCookie("userURL", result.userURL, 365);
                            window.location.href='/';
                            break;
                        case false:
                            $("#tipLogin").text(result.msg);
                            break;
                        case "captchaError":
                            $("#tipLogin").text(result.msg);
                            break;
                    }
                }
            });
        }
    },

    submitComment: function (oId) {
        var validateData = [{
            "id": "commentContentReply",
            "type": "empty",
            "tip": this.labels.commentCannotEmptyLabel
        }],
        replyTag = "Reply";
        
        if (oId === undefined) {
            validateData[0].id = "commentContent";
            replyTag = "";
        } 
        if (Util.validateForm("tip", validateData)) {
            var requestJSONObject = {
                "oId": this.oId,
                "commentContent": $("#commentContent" + replyTag).val(),
                "userName": Util.readCookie("userName"),
                "userEmail": Util.readCookie("userEmail"),
                "userURL": Util.readCookie("userURL")
            };
            if (replyTag === "Reply") {
                requestJSONObject.commentOriginalCommentId = oId;
            }
            $.ajax({
                url: "/user-add-comment",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    switch(result.sc) {
                        case true:
                            window.location.reload();
                            break;
                        case false:
                            $("#tip").text(result.msg);
                            break;
                        default:
                            $("#tip").text(result.msg);
                            break;
                    }
                }
            });
        }
    },

    replyComment: function (oId) {
        if ($("#" + oId + "commentForm").length === 0) {
            $("#" + this.originalId + "commentForm").remove();
            var replyCommentHTML =
            '<table id="' + oId + 'commentForm" class="form" width="100%">\
                \<tr>\
                    \<th width="96px">' + this.labels.commentLabel + '</th>\
                    \<td>\
                        \<textarea id="commentContentReply"></textarea>\
                    \</td>\
                \</tr>\
                \<tr>\
                    \<th colspan="2">\
                        \<span class="red" id="tipReply"></span>\
                        \<button onclick="index.submitComment(' + oId + ');">' + this.labels.submitLabel + '</button>\
                    \</th>\
                \</tr>\
            \</table>';
            $("#" + oId + "comment").append(replyCommentHTML);
            this.bindSubmitAction(oId + "commentForm");
            this.originalId = oId;
        }
        $("#" + oId + "commentForm #commentContentReply").focus();
    }
});