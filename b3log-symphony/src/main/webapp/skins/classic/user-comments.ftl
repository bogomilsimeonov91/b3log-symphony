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
        <script type="text/javascript" src="/js/util.js"></script>
        <script type="text/javascript" src="/js/user.js"></script>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="symphony-content user-comments">
            <table cellpadding="0" cellspacing="0" width="100%" class="table">
                <caption class="bigger-font">
                    ${commentLabel}
                </caption>
                <tr>
                    <th width="200px">
                        ${titleLabel}
                    </th>
                    <th width="410px">
                        ${commentContentLabel}
                    </th>
                    <th width="127px" colspan="2">
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
                    <td class="small-font" width="95px">
                        ${comment.commentDate?string('yyyy-MM-dd HH:mm:ss')}
                    </td>
                    <td width="32px">
                        <a target="_blank" href="/entries/${comment.commentEntryId}#${comment.oId}comment">
                            <span class="comment-icon"></span>
                        </a>
                    </td>
                </tr>
                </#list>
            </table>
            <#if paginationPageNums?size != 0>
            <div id="pagination">
                <#if paginationPageNums?first != 1>
                <a href="/user-comments?p=1" title="${firstPageLabel}"><<</a>
                <a id="previousPage"
                   href="/user-comments?p={paginationPageCount}"
                   title="${previousPageLabel}"><</a>
                </#if>
                <#list paginationPageNums as page>
                <a href="/user-comments?p=${page}" title="${page}">${page}</a>
                </#list>
                <#if paginationPageNums?last!=paginationPageCount>
                <a id="nextPage"
                   href="/user-comments?p={paginationPageCount}"
                   title="${nextPagePabel}">></a>
                <a href="/user-comments?p=${paginationPageCount}"
                   title="${lastPageLabel}">>></a>
                </#if>
                ${sumLabel} ${paginationPageCount} ${pageLabel}
            </div>
            </#if>
            <div class="comment-tip">
                <#list comments as comment>
                <div class="article-body none" id="${comment.oId}comment">
                    ${comment.commentContent}
                </div>
                </#list>
            </div>
        </div>
        <div class="footer">
            <#include "user-footer.ftl"/>
        </div>
        <script type="text/javascript">
            var user = new User();
            Util.initStatus();
            Util.initPagination();
            user.commentTip();
        </script>
    </body>
</html>
