<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${titleIndex}</title>
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
        <script type="text/javascript" src="/js/index.js"></script>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="header">
            <#include "header.ftl"/>
        </div>
        <div class="content">
            <button onclick="testAddArticle()">Test Add Article</button>
            <#list tags as tag>
            <dl>
                <dd>
                    <h2 title="${tag.tagTitle}">
                        <a href="/tags/${tag.tagTitle}">${tag.tagTitle}</a>
                    </h2>
                    <span>
                        <#list tag.tagTopAuthors as topAuthor>
                        <#if topAuthor.userURL != "">
                        <a title="${topAuthor.userName}" href="http://${topAuthor.userURL}" target="_blank">
                            ${topAuthor.userName}</a>
                        <#else>
                        ${topAuthor.userName}
                        </#if>
                        </#list>
                    </span>
                    <span class="right">
                        <span class="left">${tagRefCountLabel}: ${tag.tagReferenceCount}</span>
                        <span class="comment-icon" title="${commentCountLabel}"></span>
                        <span class="left">${tag.tagCommentCount}</span>
                    </span>
                </dd>
                <#list tag.tagArticles as article>
                <dd>
                    <h3 title="${article.articleTitle}">
                        <a href="/entries/${article.oId}">
                            ${article.articleTitle}</a>
                    </h3>
                    <#list article.articleTags?split(',') as tagTitle>
                    <h4 title="${tagTitle}">
                        <a href="/tags/${tagTitle}">${tagTitle}</a>
                    </h4>
                    </#list>
                    <span class="right">
                        <span class="comment-icon" title="${commentCountLabel}"></span>
                        <span class="left"> ${article.articleCommentCount}</span>
                    </span>
                    <span>
                        <#if article.articleAuthorURL != "">
                        <a title="${article.articleAuthorName}" href="http://${article.articleAuthorURL}" target="_blank">
                            ${article.articleAuthorName}</a>
                        <#else>
                        ${article.articleAuthorName}
                        </#if>
                    </span>
                    <span>
                        ${createDateLabel}: ${article.articleCreateDate?string('yyyy-MM-dd HH:mm:ss')} /
                        <#if "1970" != article.articleLastCmtDate?string('yyyy')>
                        ${lastCommentDateLabel}: ${article.articleLastCmtDate?string('yyyy-MM-dd HH:mm:ss')}
                        <#else>
                        <a href="/entries/${article.oId}#cmt">${sofaLabel}</a>
                        </#if>
                    </span>
                </dd>
                </#list>
            </dl>
            </#list>
        </div>
        <div class="footer">
            <#include "footer.ftl"/>
        </div>
        <script type="text/javascript">
            var index = new Index({
                "labels": {
                    "loginLabel": "${loginLabel}",
                    "logoutLabel": "${logoutLabel}",
                    "adminConsoleLabel": "${adminConsoleLabel}"
                }
            });
            index.initStatus();
            function testAddArticle() {
                var requestJSONObject = {
                    "article": {
                        "articleTitle": "title",
                        "articlePermalink": "/permalink",
                        "articleTags": "tag1, tag2, ....",
                        "articleAuthorEmail": "DL88250@gmail.com",
                        "articleContent": "content"
                    },
                    "host": "test.com",
                    "version": "0.2.5",
                    "from": "B3log Solo"
                };

                $.ajax({
                    url: "/add-article",
                    type: "POST",
                    data: JSON.stringify(requestJSONObject),
                    success: function(result, textStatus){
                        window.location.reload();
                    }
                });
            }
        </script>
    </body>
</html>
