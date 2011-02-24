<!DOCTYPE html>
<html>
    <head>
        <title>${titleUser}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta name="robots" content="none"/>
        <link type="text/css" rel="stylesheet" href="/styles/base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/classic/default-index.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/json2.js"></script>
        <script type="text/javascript" src="/js/lib/jtbceditor/jtbcEditor.js"></script>
        <script type="text/javascript" src="/js/util.js"></script>
        <script type="text/javascript" src="/js/user.js"></script>
        <script type="text/javascript" src="/js/lib/completed.js"></script>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="symphony-content">
            <div class="form" id="postEntryForm" width="100%">
                <label for="title">${titleLabel}</label>
                <input id="title"/>
                <label for="content">${contentLabel}</label>
                <textarea id="content" style="width: 100%;height: 360px;"></textarea>
                <label for="tags">${tagsLabel}</label>
                <input id="tags" value="" class="marginB5"/>
                <div align="right" class="marginT12">
                    <span class="tip" id="tip"></span>
                    <button id="addEntryButton" onclick="user.postEntry();">
                        ${submitLabel}
                    </button>
                </div>
            </div>
        </div>
        <div class="footer">
            <#include "footer.ftl"/>
        </div>
        <iframe name="hideFrame" class="none" id="hideFrame"></iframe>
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
