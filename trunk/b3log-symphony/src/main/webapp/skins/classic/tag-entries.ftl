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
            <h1>${tagTitle}</h1>
            <dl>
                <#list articles as article>
                <dd>
                    <span>
                        <a href="/entries/${article.oId}">${article.articleTitle}</a>
                    </span>
                    <span>
                        <#list article.articleTags?split(',') as tagTitle>
                        <a href="/tags/${tagTitle}">${tagTitle}</a>
                        </#list>
                    </span>
                    <span>
                        ${article.articleCommentCount}
                    </span>
                    <span>
                        <#if article.articleAuthorURL != "">
                        <a href="http://${article.articleAuthorURL}">${article.articleAuthorName}</a>
                        <#else>
                        ${article.articleAuthorName}
                        </#if>
                    </span>
                    <span>
                        ${article.articleCreateDate?string('yyyy-MM-dd HH:mm:ss')}/last comment date
                    </span>
                </dd>
                </#list>
            </dl>
            <#list paginationPageNums as page>
            <a href="/tags/${tagTitle}?p=${page}">${page}</a>
            </#list>
            ${sumLabel}${paginationPageCount}${pageLabel}
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
        </script>
    </body>
</html>
