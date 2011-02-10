<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${titleIndex}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta name="author" content="B3log Team"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta name="revised" content="B3log, 2011"/>
        <meta http-equiv="Window-target" content="_top"/>
        <link type="text/css" rel="stylesheet" href="/styles/base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/classic/default-index.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/json2.js"></script>
        <script type="text/javascript" src="/js/lib/jtbceditor/jtbcEditor.js"></script>
        <script type="text/javascript" src="/js/util.js"></script>
        <script type="text/javascript" src="/js/index.js"></script>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="content entry">
            <dl class="marginB20">
                <dd>
                    <div class="title">
                        <h1>
                            <a href="/entries/${article.oId}">${article.articleTitle}</a>
                        </h1>
                        by
                        <#if article.articleAuthorURL != "">
                        <a title="${article.articleAuthorName}" href="http://${article.articleAuthorURL}" target="_blank">
                            ${article.articleAuthorName}</a>
                        <#else>
                        ${article.articleAuthorName}
                        </#if>
                    </div>
                    <img src="${article.articleAuthorThumbnailURL}" class="big-head-img left"
                         alt="${article.articleAuthorName}" title="${article.articleAuthorName}"/>
                    <div class="left entry-main">
                        <div class="marginB5">
                            <div class="left">
                                <#list article.articleTags?split(',') as tagTitle>
                                <h2 title="${tagTitle}">
                                    <a href="/tags/${tagTitle?url('UTF-8')}">${tagTitle}</a><#if tagTitle_has_next>,</#if>
                                </h2>
                                </#list>
                            </div>
                            <div class="right">
                                <a href="${article.oId}#comments" title="${commentCountLabel}">
                                    <span class="comment-icon"></span>
                                    <span class="left">&nbsp;${article.articleCommentCount}</span>
                                </a>
                                <span class="left">&nbsp;|&nbsp;</span>
                                <span class="date-icon" title="${createDateLabel}"></span>
                                <span class="left">&nbsp;${article.articleCreateDate?string('yyyy-MM-dd HH:mm')}</span>
                            </div>
                            <span class="clear"></span>
                        </div>
                        <div class="article-body">
                            ${article.articleContent}
                        </div>
                        <div class="premarlink">
                            ${articlePermalinkLabel}:
                            <a href="${article.articlePermalink}" target="_blank">${article.articlePermalink}</a>
                        </div>
                        <div class="sign">
                            ${article.sign}
                        </div>
                    </div>
                    <div class="clear"></div>
                </dd>
            </dl>
            <div id="comments">
                <dl>
                    <#list article.articleComments as comment>
                    <dd id="${comment.oId}comment">
                        <img src="${comment.commentThumbnailURL}" class="middle-head-img left"
                             alt="${comment.commenterName}" title="${comment.commenterName}"/>
                        <div class="left main">
                            <div class="marginB5">
                                <span class="left">
                                    by
                                    <#if comment.commenterURL != "">
                                    <a href="http://${comment.commenterURL}" target="_blank">${comment.commenterName}</a>
                                    <#else>
                                    ${comment.commenterName}
                                    </#if>
                                </span>
                                <span class="right">
                                    <a title="${replyLabel}" href="javascript:index.replyComment('${comment.oId}');">
                                        <span class="reply-icon"></span>
                                    </a>
                                    <span class="left">&nbsp;|&nbsp;</span>
                                    <span class="date-icon" title="${createDateLabel}"></span>
                                    <span class="left">&nbsp;${comment.commentDate?string('yyyy-MM-dd HH:mm')}</span>
                                </span>
                                <span class="clear"></span>
                            </div>
                            <div class="article-body comment">
                                ${comment.commentContent}
                            </div>
                            <div class="sign">
                                ${comment.sign}
                            </div>
                        </div>
                        <span class="clear"></span>
                    </dd>
                    </#list>
                </dl>
                <#if paginationPageNums?size != 0>
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
                    ${sumLabel}${paginationPageCount}${pageLabel}
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
                            <button onclick="index.submitComment();">
                                ${submitLabel}
                            </button>
                        </th>
                    </tr>
                </table>
            </div>
        </div>
        <div class="footer">
            <#include "footer.ftl"/>
        </div>
        <script type="text/javascript">
            var index = new Index({
                "labels": {
                    "captchaCannotEmptyLabel": "${captchaCannotEmptyLabel}",
                    "commentCannotEmptyLabel": "${commentCannotEmptyLabel}",
                    "commentLabel": "${commentLabel}",
                    "captchaLabel": "${captchaLabel}",
                    "submitLabel": "${submitLabel}",
                    "loginLabel": "${loginLabel}",
                    "logoutLabel": "${logoutLabel}",
                    "adminConsoleLabel": "${adminConsoleLabel}"
                },
                "oId": "${article.oId}",
                "paginationPageCount": "${paginationPageCount}"
            });
            index.initStatus();
            Util.initPagination();
            $("#commentContent").val("");
            var editor = new jtbcEditor('commentContent');
            editor.tInit('editor', '/js/lib/jtbceditor/');
        </script>
    </body>
</html>
