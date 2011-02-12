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
            <#if paginationPageNums?size != 0>
            <div id="pagination">
                <#if paginationPageNums?first != 1>
                <a href="javascript:window.location.href='/users/' + Util.readCookie('userName') + '?p=1'" title="${firstPageLabel}"><<</a>
                <a id="previousPage"
                   href="javascript:window.location.href='/users/' + Util.readCookie('userName') + '?p={paginationPageCount}'"
                   title="${previousPageLabel}"><</a>
                </#if>
                <#list paginationPageNums as page>
                <a href="javascript:window.location.href='/users/' + Util.readCookie('userName') + '?p=${page}'" title="${page}">${page}</a>
                </#list>
                <#if paginationPageNums?last!=paginationPageCount>
                <a id="nextPage"
                   href="javascript:window.location.href='/users/' + Util.readCookie('userName') + '?p={paginationPageCount}'"
                   title="${nextPagePabel}">></a>
                <a href="javascript:window.location.href='/users/' + Util.readCookie('userName') + '?p=${paginationPageCount}'"
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
                    "nameErrorLabel": "${nameErrorLabel}"
                }
            });
            user.initStatus();
            Util.initPagination();
        </script>
    </body>
</html>
