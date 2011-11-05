<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${aboutLabel} - ${titleIndex}">
        <meta name="keywords" content="${aboutLabel},${titleIndex}"/>
        <meta name="description" content="${metaDescription}"/>
        </@head>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="symphony-content about">
            <#include "header.ftl"/>
            <div>
                ${aboutContentLabel}
            </div>
        </div>
        <#include "footer.ftl"/>
        <script type="text/javascript">
            Util.initStatus();
        </script>
    </body>
</html>
