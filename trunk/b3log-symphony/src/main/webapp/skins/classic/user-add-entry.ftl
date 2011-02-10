<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${titleUser}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="robots" content="none"/>
        <link type="text/css" rel="stylesheet" href="/styles/base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/classic/default-index.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/json2.js"></script>
        <script type="text/javascript" src="/js/util.js"></script>
        <script type="text/javascript" src="/js/user.js"></script>
    </head>
    <body>
        <#include "user-top.ftl"/>
        <div class="header">
            <#include "user-header.ftl"/>
        </div>
        <div class="content">
            <table class="form" id="postEntryForm">
                <tr>
                    <th>
                        ${titleLabel}
                    </th>
                    <td>
                        <input id="title"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${tagsLabel}
                    </th>
                    <td>
                        <input id="tags"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${contentLabel}
                    </th>
                    <td>
                        <input id="content"/>
                    </td>
                </tr>
                <tr>
                    <th colspan="2">
                        <span class="tip" id="tip"></span>
                        <button onclick="index.postEntry();">
                            ${submitLabel}
                        </button>
                    </th>
                </tr>
            </table>
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
            user.initStatus();
            Util.bindSubmitAction("postEntryForm");
        </script>
    </body>
</html>
