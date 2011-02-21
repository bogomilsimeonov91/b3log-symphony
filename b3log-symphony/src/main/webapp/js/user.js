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
    setUserInfo: function () {
        if (Util.validateForm("tipUserInfo", [{
            "id": "userName",
            "type": "length",
            "tip": this.labels.nameErrorLabel
        }, {
            "id": "userQQNum",
            "type": "empty",
            "tip": this.labels.nameErrorLabel
        }])) {
            var requestJSONObject = {
                "userName": $("#userName").val(),
                "userQQNum": $("#userQQNum").val()
            },
            changeSuccLabel = this.labels.changeSuccLabel;

            $.ajax({
                url: "/user-settings?action=basic",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    switch (result.sc) {
                        case true:
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
        var labels = this.labels;
        $("#userImg").html("<img src='" + $("#userThumbnailURL").val() + "'"
            + "class='big-head-img' alt='" + $("#userName").val()
            + "' title='" + $("#userName").val() + "'/>");
        $("#userImg img")[0].onerror = function () {
            $("#tip").text(labels.imgErrorLabel);
            return;
        }
        $("#userImg img")[0].onload = function () {
            var requestJSONObject = {
                "userThumbnailURL": $("#userThumbnailURL").val(),
                "sign": $("#sign").val(),
                "userURL": $("#userURL").val()
            };
            $.ajax({
                url: "/user-settings?action=advanced",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    switch (result.sc) {
                        case true:
                            $("#tip").text(labels.changeSuccLabel);
                            $("#userImg").html("<img src='" + $("#userThumbnailURL").val() + "'"
                                + "class='big-head-img' alt='" + $("#userName").val()
                                + "' title='" + $("#userName").val() + "'/>");
                            break;
                        case false:
                        default:
                            $("#tip").text(result.msg);
                            break;
                    }
                }
            });
        }
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
            var editorUBB = editor.tGetUBB();
            if (editorUBB.replace(/(^\s*)|(\s*$)/g, "") !== "[br]"
                && editorUBB.replace(/(^\s*)|(\s*$)/g, "").replace(/\[p\]\[br\]\[\/p\]/g, "") !== "") {
                var requestJSONObject = {
                    "article": {
                        "articleTitle": $("#title").val(),
                        "articleTags": $("#tags").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "articleContent": editorUBB
                    }
                };

                $("#addEntryButton").attr("disabled", "true");
                $("#tip").html("<img src='/skins/classic/images/loading.gif' alt='"
                    + this.labels.loadingLabel + "' title='" + this.labels.loadingLabel + "'/>");

                $.ajax({
                    url: "/user-add-entry",
                    type: "POST",
                    data: JSON.stringify(requestJSONObject),
                    success: function(result, textStatus){
                        if (result.sc) {
                            window.location.href = "/user-entries";
                        } else {
                            $("#addEntryButton").removeAttr("disabled");
                            $("#tip").html(result.msg);
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
    },

    commentTip: function () {
        $(".comment-hidden").each(function () {
            $(this).hover(function (event) {
                var pos = $(this).position();
                $("#" + this.id + "comment").show().css({
                    "left": pos.left,
                    "top": pos.top + 32
                });
            }, function () {
                $("#" + this.id + "comment").hide();
            });
        });
    }
});