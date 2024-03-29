<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${article.articleTitle} - ${titleIndex}">
        <meta name="keywords" content="${article.articleTitle},${titleIndex}"/>
        <meta name="description" content="${metaDescription}"/>
        </@head>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="symphony-content entry">
            <#include "header.ftl"/>
            <dl class="marginBottom20">
                <dd>
                    <a href="/users/${article.articleAuthorName}">
                        <img src="${article.articleAuthorThumbnailURL}" class="big-head-img left"
                             alt="${article.articleAuthorName}" title="${article.articleAuthorName}"/>
                    </a>
                    <div class="left entry-main">
                        <div>
                            <h1>
                                <a href="/entries/${article.oId}" class="bigger-font">
                                    ${article.articleTitle}
                                </a>
                            </h1>
                            by
                            <#if article.articleAuthorURL != "">
                            <a title="${article.articleAuthorName}" href="http://${article.articleAuthorURL}" target="_blank">
                                ${article.articleAuthorName}</a>
                            <#else>
                            ${article.articleAuthorName}
                            </#if>
                            &nbsp;
                            <div class="right">
                                <span class="left small-font">
                                    &nbsp;${article.articleCreateDate?string('yyyy-MM-dd HH:mm')}&nbsp;
                                </span>
                                <a href="${article.oId}#comments" title="${commentCountLabel}">
                                    <span class="comment-icon"></span>
                                    <span class="left">&nbsp;${article.articleCommentCount}</span>
                                </a>
                            </div>
                            <span class="clear"></span>
                        </div>
                        <#list article.articleTags?split(',') as tagTitle>
                        <h2 title="${tagTitle}" class="middle-font">
                            <a href="/tags/${tagTitle?url('UTF-8')}">${tagTitle}</a></h2><#if tagTitle_has_next>,</#if>
                        </#list>
                    </div>
                    <span class="clear"></span>
                    <div class="article-body">
                        ${article.articleContent}
                    </div>
                    <div class="premarlink">
                        <#if "B3log Symphony" != article.articleFrom>
                        ${from1Label}<a href="http://${article.host}" target="_blank">${article.fromTitle}</a>
                        <br/>
                        ${originalArticleLink1Label}<#else>
                        ${articlePermalink1Label}</#if><a href="${article.articlePermalink}" target="_blank">${article.articlePermalink}</a>
                    </div>
                    <a href="javascript:index.postToWb();">
                        <img alt="${postToWbLabel}" title="${postToWbLabel}"
                             src="http://v.t.qq.com/share/images/s/b24.png" class="right marginRigth5"/>
                    </a>
                    <span class="clear"></span>
                    <#if "" != article.sign>
                    <div class="sign">
                        ${article.sign}
                    </div>
                    </#if>
                </dd>
            </dl>
            <div id="comments">
                <#if paginationPageNums?size != 0>
                <dl class="entry-list">
                    <#list article.articleComments as comment>
                    <dd id="${comment.oId}comment">
                        <a href="/users/${comment.commenterName}">
                            <img src="${comment.commentThumbnailURL}" class="middle-head-img left"
                                 alt="${comment.commenterName}" title="${comment.commenterName}"/>
                        </a>
                        <div class="left title-tag">
                            <div class="marginRigth5">
                                <span class="left">
                                    by
                                    <#if comment.commenterURL != "">
                                    <a href="http://${comment.commenterURL}" target="_blank">${comment.commenterName}</a>
                                    <#else>
                                    ${comment.commenterName}
                                    </#if>
                                </span>
                                <span class="right">
                                    <span class="left small-font">
                                        ${comment.commentDate?string('yyyy-MM-dd HH:mm')}&nbsp;&nbsp;&nbsp;
                                    </span>
                                    <a title="${replyLabel}" href="javascript:index.replyComment('${comment.oId}');">
                                        <span class="reply-icon"></span>
                                    </a>
                                    <span class="left">&nbsp;</span>
                                </span>
                                <span class="clear"></span>
                            </div>
                            <div class="article-body comment">
                                ${comment.commentContent}
                            </div>
                            <#if "" != comment.sign>
                            <div class="sign">
                                ${comment.sign}
                            </div>
                            </#if>
                        </div>
                        <span class="clear"></span>
                    </dd>
                    </#list>
                </dl>
                <div id="pagination">
                    <#if paginationPageNums?first != 1>
                    <a href="/entries/${article.oId}?p=1#comments" title="${firstPageLabel}"><<</a>
                    <a id="previousPage"
                       href="/entries/${article.oId}?p={paginationPageCount}#comments"
                       title="${previousPageLabel}"><</a>
                    </#if>
                    <#list paginationPageNums as page>
                    <a href="/entries/${article.oId}?p=${page}#comments" title="${page}">${page}</a>
                    </#list>
                    <#if paginationPageNums?last!=paginationPageCount>
                    <a id="nextPage"
                       href="/entries/${article.oId}?p={paginationPageCount}#comments"
                       title="${nextPagePabel}">></a>
                    <a href="/entries/${article.oId}?p=${paginationPageCount}#comments"
                       title="${lastPageLabel}">>></a>
                    </#if>
                    ${sumLabel} ${paginationPageCount} ${pageLabel}
                </div>
                </#if>
                <table id="commentForm" class="form" width="100%" cellpadding="0" cellspacing="10">
                    <tr>
                        <td>
                            <b>${commentLabel}</b>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <textarea id="commentContent" style="width: 100%;height: 100px;"></textarea>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            <span class="tip" id="tip"></span>
                            <button id="entryCommentButton" onclick="index.submitEntryComment();">
                                ${submitLabel}
                            </button>
                        </th>
                    </tr>
                </table>
            </div>
        </div>
        <#include "footer.ftl"/>
        <script type="text/javascript" src="/js/lib/json2.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/lib/jtbceditor/jtbcEditor.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/index.js" charset="utf-8"></script>
        <script type="text/javascript">
            var index = new Index({
                "labels": {
                    "captchaCannotEmptyLabel": "${captchaCannotEmptyLabel}",
                    "commentCannotEmptyLabel": "${commentCannotEmptyLabel}",
                    "captchaLabel": "${captchaLabel}",
                    "submitLabel": "${submitLabel}",
                    "loginLabel": "${loginLabel}",
                    "logoutLabel": "${logoutLabel}",
                    "settingsLabel": "${settingsLabel}",
                    "postEntryLabel": "${postEntryLabel}",
                    "loadingLabel": "${loadingLabel}",
                    "loginFirstLabel": "${loginFirstLabel}"
                },
                "oId": "${article.oId}",
                "paginationPageCount": "${paginationPageCount}",
                "postToWbTitle": "${article.articleTitle} - ${titleIndex}"
            });
            Util.initStatus();
            Util.initPagination();
            var editor = new jtbcEditor('commentContent'),
            editorReply = undefined;
            editor.tInit('editor', '/js/lib/jtbceditor/', 'classic');
            index.commentShow();
        </script>
    </body>
</html>
