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

var Util = {
    initStatus: function () {
        if ($.browser.msie) {
            if ($.browser.version === "6.0") {
                alert("Let's kill IE6!");
                return;
            }
        }
        
        $.ajax({
            url: "/check-login",
            type: "POST",
            success: function(result, textStatus){
                switch(result.sc) {
                    case true:
                        $("#userStatus span")[0].innerHTML = result.userName;
                        $("#userStatus span").last().click(function () {
                            window.location.href = result.logoutURL;
                        });
                        break;
                    case false:
                        $("#userStatus").html("<span onclick=\"window.location.href='"
                            + result.loginURL + "'\" class='login-icon'></span>");
                        $("#commentForm").hide();
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

    validateForm: function (tipId, objects) {
        for (var i = 0; i < objects.length; i++) {
            var type = objects[i].type,
            obj = objects[i];
            var $obj = $("#" + obj.id);
            var value = $obj.val().replace(/(^\s*)|(\s*$)/g, "");
            switch (type) {
                case "email":
                    if (value === "" || !/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(value)) {
                        $("#" + tipId).text(obj.tip);
                        $obj.focus();
                        return false;
                    }
                    break;
                case "length":
                    if (value.length < 2 || value.length > 20) {
                        $("#" + tipId).text(obj.tip);
                        $obj.focus();
                        return false;
                    }
                    break;
                case "empty":
                    if (value === "") {
                        $("#" + tipId).text(obj.tip);
                        $obj.focus();
                        return false;
                    }
                    break;
                default:
                    alert("No this type for validate!");
                    break;
            }
        }
        return true;
    },
    
    initPagination: function () {
        var currentPage = 0,
        search = window.location.search;
        if (search === "") {
            currentPage = 1;
        } else {
            currentPage = parseInt(search.split('p=')[1]);
        }

        $("#pagination a").each(function () {
            var $it = $(this);
            if (currentPage === parseInt($it.text())) {
                $it.addClass("current");
            } else {
                $it.removeClass("current");
            }
        });
        
        if ($("#nextPage").length > 0) {
            $("#nextPage").attr("href", $("#nextPage").attr("href").replace("{paginationPageCount}", currentPage + 1));
        }
        if ($("#previousPage").length > 0) {
            $("#previousPage").attr("href", $("#previousPage").attr("href").replace("{paginationPageCount}", currentPage - 1));
        }
    }
};