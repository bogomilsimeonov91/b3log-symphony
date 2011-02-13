<!DOCTYPE html>
<html>
    <head>
        <title>${aboutLabel} - ${titleIndex}</title>
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
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="content">
            ${aboutContentLabel}
        </div>
        <div class="footer">
            <#include "footer.ftl"/>
        </div>
    </body>
</html>
