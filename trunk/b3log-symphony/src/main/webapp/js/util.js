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

var Util = function (args) {
    $.extend(this, args);
}

$.extend(Util.prototype, {
    initLogin: function () {
        if (true) {
            $("#userStatus").html("<span onclick=\"window.location='register'\" class='login-icon'></span>");
        }
        else {
            
    }
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
        var email = $("#email").val().replace(/(^\s*)|(\s*$)/g, ""),
        password = $("#password").val().replace(/(^\s*)|(\s*$)/g, ""),
        userName = $("#userName").val().replace(/(^\s*)|(\s*$)/g, "");
        if (userName.length > 20 || userName.length < 2) {
            $("#tip").text(this.labels.nameTooLongLabel);
            $("#userName").focus();
        } else if (email === "" || !/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(email)) {
            $("#tip").text(this.labels.emailErrorLabel);
            $("#email").focus();
        } else if (password === "") {
            $("#tip").text(this.labels.passwordEmptyLabel);
            $("#password").focus();
        } else if (password !== $("#confirmPassword").val()) {
            $("#tip").text(this.labels.passwordNoMatchLabel);
            $("#password").focus();
        } else if ($("#captcha").val().replace(/(^\s*)|(\s*$)/g, "") === "") {
            $("#tip").text(this.labels.captchaCannotEmptyLabel);
            $("#captcha").focus();
        } else {
            var requestJSONObject = {
                "captcha": $("#captcha").val(),
                "userName": $("#userName").val(),
                "userEmail": $("#email").val(),
                "userPassword": $("#password").val()
            };

            $.ajax({
                url: "/register",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    switch(result.sc) {
                        case "succ":
                            Cookie.createCookie("userName", userName, 365);
                            Cookie.createCookie("userEmail", email, 365);
                            Cookie.createCookie("userURL", "", 365);
                            window.location.href = '/';
                            break;
                        case "duplicated":
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
        var email = $("#emailLogin").val().replace(/(^\s*)|(\s*$)/g, ""),
        password = $("#passwordLogin").val().replace(/(^\s*)|(\s*$)/g, "");
        if (email === "" || !/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(email)) {
            $("#tipLogin").text(this.labels.emailErrorLabel);
            $("#emailLogin").focus();
        } else if (password === "") {
            $("#tipLogin").text(this.labels.passwordEmptyLabel);
            $("#passwordLogin").focus();
        } else if ($("#captchaLogin").val().replace(/(^\s*)|(\s*$)/g, "") === "") {
            $("#tipLogin").text(this.labels.captchaCannotEmptyLabel);
            $("#captchaLogin").focus();
        } else {
            var requestJSONObject = {
                "captcha": $("#captchaLogin").val(),
                "userEmail": email,
                "userPassword": password
            };
            $.ajax({
                url: "/login",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    switch(result.sc) {
                        case "succ":
                            Cookie.createCookie("userName", result.userName, 365);
                            Cookie.createCookie("userEmail", email, 365);
                            Cookie.createCookie("userURL", result.userURL, 365);
                            window.location.href='/';
                            break;
                        case "failed":
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

    validateComment: function (isReply) {
        var validateTag = false,
        replyTag = "";
        if (isReply) {
            replyTag = 'Reply'
        }
        if ($("#commentContent" + replyTag).val().replace(/(^\s*)|(\s*$)/g, "") === "") {
            $("#tip" + replyTag).text(this.labels.commentCannotEmptyLabel);
            $("#commentContent" + replyTag).focus();
        } else {
            validateTag = true;
        }
        return validateTag;
    },

    submitComment: function (oId) {
        var isReply = true,
        replyTag = "Reply";
        if (oId === undefined) {
            isReply = false;
            oId = this.oId;
            replyTag = "";
        }
        if (this.validateComment(isReply)) {
            var requestJSONObject = {
                "oId": oId.toString(),
                "commentContent": $("#commentContent" + replyTag).val(),
                "userName": Cookie.readCookie("userName"),
                "userEmail": Cookie.readCookie("userEmail"),
                "userURL": Cookie.readCookie("userURL")
            };
            $.ajax({
                url: "/add-comment",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    switch(result.sc) {
                    }
                }
            });
        }
    },

    replyComment: function (oId) {
        if ($("#" + oId + "commentForm").length === 0) {
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
                        \<button onclick="util.submitComment(' + oId + ');">' + this.labels.submitLabel + '</button>\
                    \</th>\
                \</tr>\
            \</table>';
            $("#" + oId + "comment").append(replyCommentHTML);
            this.bindSubmitAction(oId + "commentForm");
        } 
        $("#" + oId + "commentForm #commentContentReply").focus();
    }
});

var Cookie = {
    readCookie: function (name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for(var i=0;i < ca.length;i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1,c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
        }
        return "";
    },

    eraseCookie: function (name) {
        this.createCookie(name,"",-1);
    },

    createCookie: function (name,value,days) {
        var expires = "";
        if (days) {
            var date = new Date();
            date.setTime(date.getTime()+(days*24*60*60*1000));
            expires = "; expires="+date.toGMTString();
        }
        document.cookie = name+"="+value+expires+"; path=/";
    }
};