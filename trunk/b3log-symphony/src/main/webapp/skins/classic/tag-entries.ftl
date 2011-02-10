<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
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
        <script type="text/javascript" src="/js/util.js"></script>
        <script type="text/javascript" src="/js/index.js"></script>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="header">
            <#include "header.ftl"/>
        </div>
        <div class="content">
            <h1 title="${tagTitle}">${tagTitle}</h1>
            <dl>
                <#list articles as article>
                <dd>
                    <h2 title="${article.articleTitle}">
                        <a href="/entries/${article.oId}">${article.articleTitle}</a>
                    </h2>
                    <#list article.articleTags?split(',') as tagTitle>
                    <h3 title="${tagTitle}">
                        <a href="/tags/${tagTitle}">${tagTitle}</a>
                    </h3>
                    </#list>
                    <span>
                        ${article.articleCommentCount}
                    </span>
                    <span>
                        <#if article.articleAuthorURL != "">
                        <a href="http://${article.articleAuthorURL}" target="_blank">${article.articleAuthorName}</a>
                        <#else>
                        ${article.articleAuthorName}
                        </#if>
                    </span>
                    <span>
                        ${article.articleCreateDate?string('yyyy-MM-dd HH:mm')} / last comment date
                    </span>
                </dd>
                </#list>
            </dl>
            <div id="pagination">
                <#if paginationPageNums?first != 1>
                <a href="/tags/${tagTitle}?p=1" title="${firstPageLabel}"><<</a>
                <a id="previousPage"
                   href="/tags/${tagTitle}?p={paginationPageCount}"
                   title="${previousPageLabel}"><</a>
                </#if>
                <#list paginationPageNums as page>
                <a href="/tags/${tagTitle}?p=${page}" title="${page}">${page}</a>
                </#list>
                <#if paginationPageNums?last!=paginationPageCount>
                <a id="nextPage"
                   href="/tags/${tagTitle}?p={paginationPageCount}"
                   title="${nextPagePabel}">></a>
                <a href="/tags/${tagTitle}?p=${paginationPageCount}"
                   title="${lastPageLabel}">>></a>
                </#if>
                <#if paginationPageNums?size != 0>
                ${sumLabel}${paginationPageCount}${pageLabel}
                </#if>
            </div>
        </div>
        <div class="footer">
            <#include "footer.ftl"/>
        </div>
        <script type="text/javascript">
            var index = new Index({
                "labels": {
                    "loginLabel": "${loginLabel}",
                    "logoutLabel": "${logoutLabel}",
                    "adminConsoleLabel": "${adminConsoleLabel}"
                }
            });
            index.initStatus();
            Util.initPagination();
        </script>
    </body>
</html>
