<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${notAuthorLabel} - ${titleIndex}">
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta name="robots" content="none"/>
        </@head>
    </head>
    <body>
        <#include "top.ftl"/>
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
                    ${notAuthorLabel}
                    <br/>
                    Ungeiliable!
                </div>
                <div class="right">
                    没有权限${returnLabel}
                </div>
                <div class="clear"></div>
            </div>
        </div>
        <#include "footer.ftl"/>
    </body>
</html>