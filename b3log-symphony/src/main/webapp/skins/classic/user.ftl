<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${titleUser}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="robots" content="none"/>
        <link type="text/css" rel="stylesheet" href="/styles/base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/classic/default-index.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/json2.js"></script>
        <script type="text/javascript" src="/js/util.js"></script>
        <script type="text/javascript" src="/js/index.js"></script>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="content user">
            <dl class="entry-list">
                <#list articles as article>
                <dd>
                    <div>
                        <h3 title="${article.articleTitle}">
                            <a href="/entries/${article.oId}" class="big-font">${article.articleTitle}</a>
                        </h3>
                        <span class="right">
                            <span class="left small-font">
                                ${article.articleCreateDate?string('yyyy-MM-dd HH:mm')}&nbsp;
                            </span>
                            <#if "1970" != article.articleLastCmtDate?string('yyyy')>
                            <a href="/entries/${article.oId}#comments"
                               title="${lastCommentDateLabel}：${article.articleLastCmtDate?string('yyyy-MM-dd HH:mm')}">
                                <span class="comment-icon"></span>
                                <span class="left">&nbsp;${article.articleCommentCount}</span>
                            </a>
                            <#else>
                            <span class="left">&nbsp;&nbsp;</span>
                            <a href="/entries/${article.oId}#commentContent">
                                <span class="sofa-icon" title="${sofaLabel}"></span>
                            </a>
                            <span class="left">&nbsp;</span>
                            </#if>
                        </span>
                        <span class="clear"></span>
                    </div>
                    <#list article.articleTags?split(',') as tagTitle>
                    <h4 title="${tagTitle}" class="middle-font">
                        <a href="/tags/${tagTitle?url('UTF-8')}">${tagTitle}</a><#if tagTitle_has_next>,</#if>
                    </h4>
                    </#list>
                    </#list>
                </dd>
            </dl>
            <table cellpadding="0" cellspacing="0" width="100%" class="table">
                <tr>
                    <th style="width: 200px;">
                        ${titleLabel}
                    </th>
                    <th width="361px">
                        ${commentContentLabel}
                    </th>
                    <th width="150px">
                        ${commentDateLabel}
                    </th>
                    <th width="32px">
                        ${commentLabel}
                    </th>
                </tr>
                <#list comments as comment>
                <tr>
                    <td>
                        <a href="/entries/${comment.commentEntryId}" target="_blank">
                            ${comment.commentEntryTitle}
                        </a>
                    </td>
                    <td>
                        <div class="comment-hidden" id="${comment.oId}">
                            ${comment.commentContent}
                        </div>
                    </td>
                    <td align="center">
                        ${comment.commentDate?string('yyyy-MM-dd HH:mm:ss')}
                    </td>
                    <td  align="center" style="border-color: #BBBBBB;">
                        <a target="_blank" href="/entries/${comment.commentEntryId}#${comment.oId}comment">
                            <span class="comment-icon"></span>
                        </a>
                    </td>
                </tr>
                </#list>
            </table>
        </div>
        <div class="footer">
            <#include "footer.ftl"/>
        </div>
        <script type="text/javascript">
            var index = new Index({
                "labels": {
                    "loginLabel": "${loginLabel}",
                    "logoutLabel": "${logoutLabel}",
                    "settingsLabel": "${settingsLabel}",
                    "postEntryLabel": "${postEntryLabel}"
                }
            });
            index.initStatus();
            Util.initPagination();
        </script>
    </body>
</html>
