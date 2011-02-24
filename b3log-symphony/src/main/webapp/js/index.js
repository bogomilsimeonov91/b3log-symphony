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
    bindSubmitAction: function () {
        for (var i = 0; i < arguments.length; i++) {
            $("#" + arguments[i] + ".form input").keypress(function (event) {
                if (event.keyCode === 13) {
                    $(this).parents(".form").find("button").click();
                }
            });
        }
    },

    submitEntryComment: function () {
        var editorUBB = editor.tGetUBB();
        if (editorUBB.replace(/(^\s*)|(\s*$)/g, "") !== "[br]"
            && editorUBB.replace(/(^\s*)|(\s*$)/g, "").replace(/\[p\]\[br\]\[\/p\]/g, "") !== "") {
            var requestJSONObject = {
                "oId": this.oId,
                "commentContent": editorUBB
            };
            
            $("#entryCommentButton").attr("disabled", "true");
            $("#tip").html("<img src='/skins/classic/images/loading.gif' alt='"
                + this.labels.loadingLabel + "' title='" + this.labels.loadingLabel + "'/>");
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
                        default:
                            $("#entryCommentButton").removeAttr("disabled");
                            $("#tip").html(result.msg);
                            break;
                    }
                }
            });
        } else {
            $("#tip").text(this.labels.commentCannotEmptyLabel);
        }
    },

    submitComment: function (oId) {
        var editorUBB = editorReply.tGetUBB();
        if (editorUBB.replace(/(^\s*)|(\s*$)/g, "") !== "[br]"
            && editorUBB.replace(/(^\s*)|(\s*$)/g, "").replace(/\[p\]\[br\]\[\/p\]/g, "") !== "") {
            var requestJSONObject = {
                "oId": this.oId,
                "commentContent": editorUBB,
                "commentOriginalCommentId": oId
            };

            $("#replyCommentButton").attr("disabled", "true");
            $("#tipReply").html("<img src='/skins/classic/images/loading.gif' alt='"
                + this.labels.loadingLabel + "' title='" + this.labels.loadingLabel + "'/>");
            $.ajax({
                url: "/user-add-comment",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    switch(result.sc) {
                        case true:
                            window.location.reload();
                            $("#commentContentReply").val("");
                            break;
                        case false:
                        default:
                            $("#replyCommentButton").removeAttr("disabled");
                            $("#tipReply").html(result.msg);
                            break;
                    }
                }
            });
        } else {
            $("#tipReply").text(this.labels.commentCannotEmptyLabel);
        }
    },

    replyComment: function (oId) {
        if ($("#" + oId + "commentForm").length === 0) {
            if ($("#userStatus span")[0].innerHTML === "") {
                alert(this.labels.loginFirstLabel);
                $("#userStatus span").last().click();
                return;
            }
            $("#" + this.originalId + "commentForm").remove();
            var replyCommentHTML =
            '<table id="' + oId + 'commentForm" class="form" width="100%" cellspacing="10">\
                \<tr>\
                    \<th width="43px"></th>\
                    \<td>\
                        \<textarea style="width: 100%;height: 100px;" id="commentContentReply"></textarea>\
                    \</td>\
                \</tr>\
                \<tr>\
                    \<th colspan="2">\
                        \<span class="tip" id="tipReply"></span>\
                        \<button id="replyCommentButton" onclick="index.submitComment(' + oId + ');">' + this.labels.submitLabel + '</button>\
                    \</th>\
                \</tr>\
            \</table>';
            $("#" + oId + "comment").append(replyCommentHTML);
            this.originalId = oId;

            editorReply = new jtbcEditor('commentContentReply');
            editorReply.tInit('editorReply', '/js/lib/jtbceditor/');
        }
    },
    
    postToWb: function () {
        var _t = encodeURI(this.postToWbTitle);
        var _url = encodeURIComponent("http://www.b3log.org/entries/" + this.oId);
        var _appkey = encodeURI("295bcd39428e4c098b31dc599ad07f6d");
        var _pic = encodeURI('');//（例如：var _pic='图片url1|图片url2|图片url3....）
        var _site = 'http://www.b3log.org';//你的网站地址
        var _u = 'http://v.t.qq.com/share/share.php?url='+_url+'&appkey='+_appkey+'&site='+_site+'&pic='+_pic+'&title='+_t;
        window.open( _u,'', 'width=700, height=480, top=' +
            (screen.height - 480) / 2 + ', left=' +
            (screen.width - 700) / 2 + ', toolbar=no, menubar=no, scrollbars=no, location=yes, resizable=no, status=no' );
    },

    commentShow: function () {
        $("#comments dd").each(function () {
            var $it = $(this),
            $itRef = $it.find(".article-body>.ref");
            if ($itRef.length === 1) {
                if ($itRef[0].clientHeight > 129) {
                    $it.hover(function () {
                        var maxHeight = $itRef.attr("scrollHeight");
                        if ($itRef.height() === 129) {
                            $itRef.animate({
                                "maxHeight":maxHeight
                            }, 600);
                        }
                    }, function () {
                        $itRef.animate({
                            "maxHeight":129
                        }, 600);
                    });
                }
            }
        });
    }
});
