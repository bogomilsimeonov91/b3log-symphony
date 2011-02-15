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
        if (editor.tGetUBB().replace(/(^\s*)|(\s*$)/g, "") !== "[br]"
            && editor.tGetUBB().replace(/(^\s*)|(\s*$)/g, "") !== "[p][br][p]") {
            var requestJSONObject = {
                "oId": this.oId,
                "commentContent": editor.tGetUBB().replace("[br]", "")
            };
            //alert(editor.tGetUBB().replace("[br]", ""));
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
                            $("#tip").text(result.msg);
                            break;
                    }
                }
            });
        } else {
            $("#tip").text(this.labels.commentCannotEmptyLabel);
        }
    },

    submitComment: function (oId) {
        if (Util.validateForm("tipReply", [{
                "id": "commentContentReply",
                "type": "empty",
                "tip": this.labels.commentCannotEmptyLabel
            }])) {
            var requestJSONObject = {
                "oId": this.oId,
                "commentContent": $("#commentContentReply").val(),
                "commentOriginalCommentId": oId
            };
            $.ajax({
                url: "/user-add-comment",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    switch(result.sc) {
                        case true:
                            $("#commentContentReply").val("");
                            window.location.reload();
                            break;
                        case false:
                        default:
                            $("#tipReply").text(result.msg);
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
                '<table id="' + oId + 'commentForm" class="form" width="100%" cellspacing="10">\
                \<tr>\
                    \<th width="99px">' + this.labels.commentLabel + '</th>\
                    \<td>\
                        \<textarea id="commentContentReply"></textarea>\
                    \</td>\
                \</tr>\
                \<tr>\
                    \<th colspan="2">\
                        \<span class="tip" id="tipReply"></span>\
                        \<button onclick="index.submitComment(' + oId + ');">' + this.labels.submitLabel + '</button>\
                    \</th>\
                \</tr>\
            \</table>';
            $("#" + oId + "comment").append(replyCommentHTML);
            this.bindSubmitAction(oId + "commentForm");
            this.originalId = oId;
        }
        $("#" + oId + "commentForm #commentContentReply").focus();
    },
    
    postToWb: function () {
        var _content = $("<p>${article.articleContent}<p>").text();
        var _t = encodeURI("${article.articleTitle} - ${titleIndex}\n" + _content);
        var _url = encodeURIComponent(document.location);
        var _appkey = encodeURI("295bcd39428e4c098b31dc599ad07f6d");
        var _pic = encodeURI('');//（例如：var _pic='图片url1|图片url2|图片url3....）
        var _site = '';//你的网站地址
        var _u = 'http://v.t.qq.com/share/share.php?url='+_url+'&appkey='+_appkey+'&site='+_site+'&pic='+_pic+'&title='+_t;
        window.open( _u,'', 'width=700, height=680, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, location=yes, resizable=no, status=no' );
    }
});