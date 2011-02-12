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
        $("#userStatus span")[0].innerHTML = Util.readCookie("userName");
    },

    setUserInfo: function () {
        if (Util.validateForm("tipUserInfo", [{
            "id": "userName",
            "type": "length",
            "tip": this.labels.nameErrorLabel
        }])) {
            var requestJSONObject = {
                "userName": $("#userName").val()
            },
            changeSuccLabel = this.labels.changeSuccLabel;

            $.ajax({
                url: "/user-settings?action=basic",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    switch (result.sc) {
                        case true:
                            Util.createCookie("userName", $("#userName").val(), 365);
                            $("#tipUserInfo").text(changeSuccLabel);
                            break;
                        case false:
                        default:
                            $("#tipUserInfo").text(result.msg);
                            break;
                    }
                }
            });
        }
    },

    setUserSettings: function () {
        var requestJSONObject = {
            "sign": $("#sign").val(),
            "userURL": $("#userURL").val()
        },
        changeSuccLabel = this.labels.changeSuccLabel;

        $.ajax({
            url: "/user-settings?action=advanced",
            type: "POST",
            data: JSON.stringify(requestJSONObject),
            success: function(result, textStatus){
                switch (result.sc) {
                    case true:
                        $("#tip").text(changeSuccLabel);
                        break;
                    case false:
                    default:
                        $("#tip").text(result.msg);
                        break;
                }
            }
        });
    },

    setHead: function () {
        var labels = this.labels;
        $("#hideFrame").load(function () {
            var $iframe = $("#hideFrame").contents();
            if ($iframe.find("pre").length === 1) {
                $("#tipHead").text($iframe.find("pre").html());
            } else if ($iframe.find("#headImg").length === 1) {
                $("#tipHead").text(labels.setSuccLabel);
                $("#uploadFile").html("<input type='file' name='myFile'>");
                $("#headImg").attr("src", $iframe.find("#headImg").attr("src"));
            }
        });
    },

    postEntry: function() {
        if (Util.validateForm("tip", [{
            "id": "title",
            "type": "empty",
            "tip": this.labels.titleCannotEmptyLabel
        }, {
            "id": "tags",
            "type": "empty",
            "tip": this.labels.tagsCannotEmptyLabel
        }])) {
            if (editor.tGetUBB().replace(/(^\s*)|(\s*$)/g, "") !== "[br]") {
                var requestJSONObject = {
                    "article": {
                        "articleTitle": $("#title").val(),
                        "articleTags": $("#tags").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "articleAuthorEmail": Util.readCookie("userEmail"),
                        "articleContent": editor.tGetUBB()
                    }
                };

                $.ajax({
                    url: "/user-add-entry",
                    type: "POST",
                    data: JSON.stringify(requestJSONObject),
                    success: function(result, textStatus){
                        if (result.sc) {
                            window.location.href = "/users/" + Util.readCookie("userName");
                        } else {
                            $("#tip").text(result.msg);
                        }
                    }
                });
            } else {
                $("#tip").text(this.labels.contentCannotEmptyLabel);
            }
        }
    },

    initPostEntry: function (tags) {
        $("#title").val("");
        $("#tags").val("");
        $("#content").val("");
        var editor = new jtbcEditor('content');
        editor.tInit('editor', '/js/lib/jtbceditor/');
        $("#tags").completed({
            height: 90,
            data: tags
        });
        return editor;
    }
});