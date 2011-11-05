<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${titleUser} - ${titleIndex}">
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta name="robots" content="none"/>
        </@head>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="symphony-content">
            <div class="form" id="postEntryForm" width="100%">
                <label for="title">${titleLabel}</label>
                <input id="title" type="text" />
                <label for="content">${contentLabel}</label>
                <textarea id="content" style="width: 100%;height: 360px;"></textarea>
                <label for="tags">${tagsLabel}</label><br/>
                <input id="tags" value="" class="marginB5" type="text" />
                <div align="right" class="marginT12">
                    <span class="tip" id="tip"></span>
                    <button id="addEntryButton" onclick="user.postEntry();">
                        ${submitLabel}
                    </button>
                </div>
            </div>
        </div>
        <#include "footer.ftl"/>
        <iframe name="hideFrame" class="none" id="hideFrame"></iframe>
        <script type="text/javascript" src="/js/lib/json2.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/lib/jtbceditor/jtbcEditor.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/user.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/lib/completed.js" charset="utf-8"></script>
        <script type="text/javascript">
            var user = new User({
                "labels": {
                    "contentCannotEmptyLabel": "${contentCannotEmptyLabel}",
                    "tagsCannotEmptyLabel": "${tagsCannotEmptyLabel}",
                    "titleCannotEmptyLabel": "${titleCannotEmptyLabel}",
                    "loadingLabel": "${loadingLabel}"
                }
            });
            Util.initStatus();
            var editor = user.initPostEntry(${tags});
        </script>
    </body>
</html>
