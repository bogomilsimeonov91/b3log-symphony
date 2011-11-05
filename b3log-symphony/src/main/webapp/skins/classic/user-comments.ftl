<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${titleUser} - ${titleIndex}">
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta name="robots" content="none"/>
        </@head>
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
            <div id="commentTips">
            </div>
        </div>
        <#include "footer.ftl"/>
        <script type="text/javascript" src="/js/user.js" charset="utf-8"></script>
        <script type="text/javascript">
            var user = new User();
            Util.initStatus();
            Util.initPagination();
            user.commentTip();
        </script>
    </body>
</html>
