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

var Util = function (labels) {
    this.labels = labels;
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
        $(".form input").keypress(function (event) {
            if (event.keyCode === 13) {
                $(this).parents(".form").find("button").click();
            }
        });
    },
    
    register: function () {
        var email = $("#email").val().replace(/(^\s*)|(\s*$)/g, ""),
        password = $("#password").val().replace(/(^\s*)|(\s*$)/g, "");
        if (email === "" || !/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(email)) {
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
                            window.location.href='/';
                            break;
                        case "duplicated":
                            $("#tip").text(result.sc);
                            break;
                        case "captchaError":
                            $("#tip").text(result.sc);
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
                "userEmail": $("#emailLogin").val(),
                "userPassword": $("#passwordLogin").val()
            };

            $.ajax({
                url: "/login",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    switch(result.sc) {
                        case "succ":
                            window.location.href='/';
                            break;
                        case "duplicated":
                            $("#tipLogin").text(result.sc);
                            break;
                        case "captchaError":
                            $("#tipLogin").text(result.sc);
                            break;
                    }
                }
            });
        }
    }
});