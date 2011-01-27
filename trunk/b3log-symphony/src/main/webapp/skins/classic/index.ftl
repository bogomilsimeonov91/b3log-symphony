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
        <#include "common-top.ftl"/>
        <div class="header">
            <#include "header.ftl"/>
        </div>
        <div class="content">
            <#list tags as tag>
            <dl>
                <dd>
                    <span>
                        ${tag.tagTitle}
                    </span>
                    <span>
                        <a href="#">author1</a>,
                        <a href="#">author2</a>,
                        <a href="#">author3</a>,
                        <a href="#">author4</a>,
                        <a href="#">author5</a>
                    </span>
                    <span>
                        article count/ comment count
                    </span>
                </dd>
                <#list tag.tagArticles as article>
                <dd>
                    <span>
                        icon
                    </span>
                    <span>
                        ${article.articleTitle}
                    </span>
                    <span>
                        tags
                    </span>
                    <span>
                        comment count
                    </span>
                    <span>
                        author
                    </span>
                    <span>
                        create date/last comment date
                    </span>
                </dd>
                </#list>
            </dl>
            </#list>
        </div>
        <div class="footer">
            <#include "footer.ftl"/>
        </div>
    </body>
</html>
