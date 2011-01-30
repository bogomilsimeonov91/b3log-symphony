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
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/json2.js"></script>
        <script type="text/javascript" src="/js/util.js"></script>
    </head>
    <body>
        <#include "common-top.ftl"/>
        <div class="header">
            <#include "header.ftl"/>
        </div>
        <div class="content">
            <dl class="entry">
                <dd>
                    <div class="userInfo left">${article.articleAuthorName}</div>
                    <div class="left">
                        <div class="title">
                            <h3></h3>
                            <div>
                                <#list article.articleTags?split(',') as tagTitle>
                                <a href="/tags/${tagTitle}">${tagTitle}</a>
                                </#list>
                                ${article.articleCreateDate?string('yyyy-MM-dd HH:mm:ss')}, ${article.articleCommentCount}</div>
                        </div>
                        <div class="content">
                            <div>
                                ${article.articleContent}
                            </div>
                            <div class="sign">

                            </div>
                        </div>
                    </div>
                    <div class="clear"></div>
                </dd>
            </dl>
            <div class="comment">
                <table>
                    <tr>
                        <td>
                            ${commentLabel}
                        </td>
                        <td>
                            <textarea></textarea>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${captchaLabel}
                        </th>
                        <td>
                            <input id="captchaLogin" class="normal-input"/>
                            <img alt="captcha" src="/captcha"></img>
                        </td>
                    </tr>
                    <tr>
                        <th colspan="2">
                            <span class="red" id="tipLogin"></span>
                            <button onclick="util.login();">
                                ${submitLabel}
                            </button>
                        </th>
                    </tr>
                </table>
            </div>
        </div>
        <div class="footer">
            <#include "footer.ftl"/>
        </div>
        <script type="text/javascript">
            var util = new Util();
            util.initLogin();
        </script>
    </body>
</html>
