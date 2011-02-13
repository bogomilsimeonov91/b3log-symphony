<!DOCTYPE html>
<html>
    <head>
        <title>${titleUser}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="robots" content="none"/>
        <link type="text/css" rel="stylesheet" href="/styles/base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/classic/default-index.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/json2.js"></script>
        <script type="text/javascript" src="/js/lib/jtbceditor/jtbcEditor.js"></script>
        <script type="text/javascript" src="/js/util.js"></script>
        <script type="text/javascript" src="/js/user.js"></script>
        <script type="text/javascript" src="/js/lib/completed.js"></script>
    </head>
    <body>
        <#include "user-top.ftl"/>
        <div class="content">
            <div class="form" id="postEntryForm" width="100%">
                <label for="title">${titleLabel}</label>
                <input id="title"/>
                <label for="content">${contentLabel}</label>
                <textarea id="content" style="width: 100%;height: 360px;"></textarea>
                <label for="tags">${tagsLabel}</label>
                <input id="tags" value=""/>
                <div align="right" class="marginT12">
                    <span class="tip" id="tip"></span>
                    <button onclick="user.postEntry();">
                        ${submitLabel}
                    </button>
                </div>
            </div>
        </div>
        <div class="footer">
            <#include "user-footer.ftl"/>
        </div>
        <iframe name="hideFrame" class="none" id="hideFrame"></iframe>
        <script type="text/javascript">
            var user = new User({
                "labels": {
                    "contentCannotEmptyLabel": "${contentCannotEmptyLabel}",
                    "tagsCannotEmptyLabel": "${tagsCannotEmptyLabel}",
                    "titleCannotEmptyLabel": "${titleCannotEmptyLabel}"
                }
            });
            Util.initStatus();
            var editor = user.initPostEntry(${tags});
        </script>
    </body>
</html>
