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
        <div class="content index">
            <h1 title="${tagTitle}">${tagTitle}</h1>
            <dl>
                <#list articles as article>
                <dd>
                    <div class="user-info left">
                        <#if article.articleAuthorURL != "">
                        <a title="${article.articleAuthorName}" href="http://${article.articleAuthorURL}" target="_blank">
                            <img class="middle-head-img" src="{article.articleAuthorThumbnailURL}"
                                 title="${article.articleAuthorName}" alt="${article.articleAuthorName}"/>
                        </a>
                        <#else>
                        <img class="middle-head-img" src="{article.articleAuthorThumbnailURL}"
                             alt="${article.articleAuthorName}" title="${article.articleAuthorName}"/>
                        </#if>
                    </div>
                    <div class="left main">
                        <div class="left">
                            <h2 title="${article.articleTitle}">
                                <a href="/entries/${article.oId}">${article.articleTitle}</a>
                            </h2>
                            by
                            <#if article.articleAuthorURL != "">
                            <a href="http://${article.articleAuthorURL}" target="_blank">${article.articleAuthorName}</a>
                            <#else>
                            ${article.articleAuthorName}
                            </#if>
                        </div>
                        <div class="right">
                            <span class="date-icon" title="${createDateLabel}"></span>
                            <span class="left">&nbsp;${article.articleCreateDate?string('yyyy-MM-dd HH:mm')}</span>
                        </div>
                        <span class="clear"></span>
                        <div class="left">
                            <#list article.articleTags?split(',') as tagTitle>
                            <h3 title="${tagTitle}">
                                <a href="/tags/${tagTitle}">${tagTitle}</a><#if tagTitle_has_next>,</#if>
                            </h3>
                            </#list>
                        </div>
                        <div class="right">
                            <#if "1970" != article.articleLastCmtDate?string('yyyy')>
                            <a href="/entries/${article.oId}#comments" title="${commentCountLabel}">
                                <span class="comment-icon"></span>
                                <span class="left">&nbsp;${article.articleCommentCount}</span>
                            </a>
                            <span class="left">
                                <span class="left">&nbsp;|&nbsp;</span>
                                <span class="last-date-icon" title="${lastCommentDateLabel}"></span>
                                <span class="left">&nbsp;${article.articleLastCmtDate?string('yyyy-MM-dd HH:mm')}</span>
                            </span>
                            <#else>
                            <a href="/entries/${article.oId}#commentContent"><span class="sofa-icon" title="${sofaLabel}"></span></a>
                            </#if>
                        </div>
                        <span class="clear"></span>
                    </div>
                    <span class="clear"></span>
                </dd>
                </#list>
            </dl>
            <#if paginationPageNums?size != 0>
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
                ${sumLabel}${paginationPageCount}${pageLabel}
            </div>
            </#if>
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
