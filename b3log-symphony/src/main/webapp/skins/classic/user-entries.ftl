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
        <script type="text/javascript" src="/js/user.js"></script>
    </head>
    <body>
        <#include "user-top.ftl"/>
        <div class="header">
            <#include "user-header.ftl"/>
        </div>
        <div class="content">
            <table cellpadding="0" cellspacing="0" width="100%" class="table">
                <tr>
                    <th style="min-width: 200px;">
                        ${titleLabel}
                    </th>
                    <th width="300px">
                        ${tagsLabel}
                    </th>
                    <th width="150px">
                        ${createDateLabel}
                    </th>
                    <th width="80px">
                        ${commentCountLabel}
                    </th>
                    <th width="150px">
                        ${lastCommentDateLabel}
                    </th>
                </tr>
                <#list articles as article>
                <tr>
                    <td>
                        <a href="/entries/${article.oId}" target="_blank">${article.articleTitle}</a>
                    </td>
                    <td>
                        <#list article.articleTags?split(',') as tagTitle>
                        <a href="/tags/${tagTitle}" target="_blank">${tagTitle}</a><#if tagTitle_has_next>,</#if>
                        </#list>
                    </td>
                    <td align="center">
                        ${article.articleCreateDate?string('yyyy-MM-dd HH:mm:ss')}
                    </td>
                    <td align="center">
                        <a href="/entries/${article.oId}#comments" target="_blank"
                           title="${commentCountLabel}:${article.articleCommentCount}">
                            <span class="comment-icon"></span>
                            <span class="left">&nbsp;${article.articleCommentCount}</span>
                        </a>
                    </td>
                    <td style="border-color: #BBBBBB;" align="center">
                        <#if "1970" != article.articleLastCmtDate?string('yyyy')>
                        ${article.articleLastCmtDate?string('yyyy-MM-dd HH:mm:ss')}
                        <#else>
                        <span class="left">${noCommentLabel}&nbsp;</span>
                        <a href="/entries/${article.oId}#commentContent" target="_blank">
                            <span class="sofa-icon" title="${sofaLabel}"></span>
                        </a>
                        </#if>
                    </td>
                </tr>
                </#list>
            </table>
            <#if paginationPageNums?size != 0>
            <div id="pagination">
                <#if paginationPageNums?first != 1>
                <a href="/user-entries?p=1" title="${firstPageLabel}"><<</a>
                <a id="previousPage"
                   href="/user-entries?p={paginationPageCount}"
                   title="${previousPageLabel}"><</a>
                </#if>
                <#list paginationPageNums as page>
                <a href="/user-entries?p=${page}" title="${page}">${page}</a>
                </#list>
                <#if paginationPageNums?last!=paginationPageCount>
                <a id="nextPage"
                   href="/user-entries?p={paginationPageCount}"
                   title="${nextPagePabel}">></a>
                <a href="/user-entries?p=${paginationPageCount}"
                   title="${lastPageLabel}">>></a>
                </#if>
                ${sumLabel}${paginationPageCount}${pageLabel}
            </div>
            </#if>
        </div>
        <div class="footer">
            <#include "user-footer.ftl"/>
        </div>
        <script type="text/javascript">
            var user = new User({
                "labels": {
                    "passwordEmptyLabel": "${passwordEmptyLabel}",
                    "passwordNoMatchLabel": "${passwordNoMatchLabel}",
                    "nameTooLongLabel": "${nameTooLongLabel}"
                }
            });
            user.initStatus();
            Util.initPagination();
        </script>
    </body>
</html>
