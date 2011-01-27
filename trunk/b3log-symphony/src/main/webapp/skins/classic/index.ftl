<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${titleLabel}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta name="author" content="B3log Team"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta name="revised" content="B3log, 2011"/>
        <meta http-equiv="Window-target" content="_top"/>
        <link type="text/css" rel="stylesheet" href="/styles/base.css"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-index.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
    </head>
    <body>
        <div class="header">
            <#include "header.ftl"/>
        </div>
        <div class="content">
            <#list tags as tag>
            <div>
                <div>"TITLE": ${tag.tagTitle}</div>
                <#list tag.tagArticles as article>
                <div>
                    &nbsp;&nbsp;&nbsp;&nbsp;"ARTICLE": ${article.articleTitle}
                </div>
                </#list>
            </div>
            </#list>
        </div>
        <div class="footer">
            <#include "footer.ftl"/>
        </div>
    </body>
</html>
