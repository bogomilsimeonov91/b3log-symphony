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
        <link type="text/css" rel="stylesheet" href="/skins/classic/default-index.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/json2.js"></script>
        <script type="text/javascript" src="/js/util.js"></script>
    </head>
    <body>
        <#include "/skins/classic/common-top.ftl"/>
        <div class="header">
            <#include "admin-header.ftl"/>
        </div>
        <div class="content">
            <table class="form">
                <tr>
                    <th>
                        ${userNameLabel}
                    </th>
                    <td>
                        <input/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${originalPasswordLabel}
                    </th>
                    <td>
                        <input type="password"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${newlPasswordLabel}
                    </th>
                    <td>
                        <input type="password"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${confirmPasswordLabel}
                    </th>
                    <td>
                        <input type="password"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${signLabel}
                    </th>
                    <td>
                        <textarea></textarea>
                    </td>
                </tr>
                <tr>
                    <th colspan="2">
                        <span class="red" id="tip"></span>
                        <button onclick="util.register();">
                            ${registerLabel}
                        </button>
                    </th>
                </tr>
            </table>
        </div>
        <div class="footer">
            <#include "admin-footer.ftl"/>
        </div>
        <script type="text/javascript">
        </script>
    </body>
</html>
