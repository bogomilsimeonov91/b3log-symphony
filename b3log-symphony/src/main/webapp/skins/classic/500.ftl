<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${internalErrorLabel} - ${titleIndex}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta name="robots" content="none"/>
        <link type="text/css" rel="stylesheet" href="/styles/base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/classic/default-index.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <link href='http://fonts.googleapis.com/css?family=Reenie+Beanie' rel='stylesheet' type='text/css'/>
    </head>
    <body>
        <div id="top">
            <a href="http://${host}" class="logo" target="_blank">
                <span style="color: orange;margin-left:0px;">B</span>
                <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
                <span style="color: green;">L</span>
                <span style="color: red;">O</span>
                <span style="color: blue;">G</span>&nbsp;
                <span style="color: red; font-weight: bold;">Symphony</span>
            </a>
        </div>
        <div class="symphony-content">
            <div class="header">
                <ul class="header-navi">
                    <li>
                        <a href="/" title="${indexLabel}">${indexLabel}</a>
                    </li>
                    <li>
                        <a href="/top-entries" title="${topEntriesLabel}">${topEntriesLabel}</a>
                    </li>
                    <li>
                        <a href="/chinasb" title="${ChinasbLabel}">${ChinasbLabel2}</a>
                    </li>
                    <li>
                        <a href="/about" title="${aboutLabel}">${aboutLabel}</a>
                    </li>
                </ul>
                <div class="clear"></div>
            </div>
            <div class="error">
                <h2>
                    ${welcomeLabel}
                </h2>
                <br/>
                <div class="error-title">
                    ${internalErrorLabel}
                    <br/>
                    Ungeiliable!
                </div>
                <div class="right">
                    内部错误${returnLabel}
                </div>
                <div class="clear"></div>
            </div>
        </div>
        <div class="footer">
            <#include "footer.ftl"/>
        </div>
    </body>
</html>