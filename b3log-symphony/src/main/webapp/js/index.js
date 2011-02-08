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
                            + "' onclick=\"window.location='/user-settings'\" class='admin-icon'></span>"
                            + "<span class='left'>&nbsp;|</span>"
                            + "<span title='" + labels.logoutLabel
                            + "' onclick=\"Util.logout();\" class='logout-icon'></span>");
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
                $("#password").focus();
                return;
            }
            var requestJSONObject = {
                "captcha": $("#captcha").val().replace(/(^\s*)|(\s*$)/g, ""),
                "userName": $("#userName").val().replace(/(^\s*)|(\s*$)/g, ""),
                "userEmail": $("#email").val().replace(/(^\s*)|(\s*$)/g, ""),
                "userPassword": $("#password").val()
            };

            $.ajax({
                url: "/register",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    switch(result.sc) {
                        case true:
                            Cookie.createCookie("userName", $("#userName").val().replace(/(^\s*)|(\s*$)/g, ""), 365);
                            Cookie.createCookie("userEmail", $("#email").val().replace(/(^\s*)|(\s*$)/g, ""), 365);
                            Cookie.createCookie("userURL", "", 365);
                            window.location.href = '/';
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
            var requestJSONObject = {
                "captcha": $("#captchaLogin").val(),
                "userEmail": $("#emailLogin").val(),
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
                            Util.createCookie("userEmail", $("#emailLogin").val(), 365);
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
        replyTag = "Reply",
        paginationPageCount = this.paginationPageCount;
        
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
                url: "/add-comment",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    switch(result.sc) {
                        case true:
                            window.location.search = "p=" + paginationPageCount;
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
            '<table id="' + oId + 'commentForm" class="form">\
                \<tr>\
                    \<th>' + this.labels.commentLabel + '</th>\
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