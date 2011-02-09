<!DOCTYPE html>
<html>
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
        <div class="content index">
            <button onclick="testAddArticle()">Test Add Article</button>
            <#list tags as tag>
            <dl>
                <dt>
                    <a title="${tag.tagTitle}" href="/tags/${tag.tagTitle}">${tag.tagTitle}</a>
                    <span>
                        <#list tag.tagTopAuthors as topAuthor>
                        <#if topAuthor.userURL != "">
                        <a class="no-underline" href="http://${topAuthor.userURL}" target="_blank">
                            <img alt="${topAuthor.userName}" title="${topAuthor.userName}"
                                 src="${topAuthor.userThumbnailURL}"/>
                        </a>
                        <#else>
                        <img alt="${topAuthor.userName}" title="${topAuthor.userName}"
                             src="${topAuthor.userThumbnailURL}"/>
                        </#if>
                        </#list>
                    </span>
                    <span class="right">
                        <span class="tag-icon" title="${tagRefCountLabel}"></span>&nbsp;
                        <span class="left">${tag.tagReferenceCount}&nbsp;|&nbsp;</span>
                        <span class="comment-icon" title="${commentCountLabel}"></span>
                        <span class="left">&nbsp;${tag.tagCommentCount}</span>
                    </span>
                </dt>
                <#list tag.tagArticles as article>
                <dd>
                    <div class="user-info left">
                        <#if article.articleAuthorURL != "">
                        <a title="${article.articleAuthorName}" href="http://${article.articleAuthorURL}" target="_blank">
                            <img alt="${article.articleAuthorName}" title="${article.articleAuthorName}"
                                 src="${article.articleAuthorThumbnailURL}"/>
                        </a>
                        <#else>
                        <img alt="${article.articleAuthorName}" title="${article.articleAuthorName}"
                             src="${article.articleAuthorThumbnailURL}"/>
                        </#if>
                    </div>
                    <div class="left main">
                        <div>
                            <h2 title="${article.articleTitle}">
                                <a href="/entries/${article.oId}">
                                    ${article.articleTitle}</a>
                            </h2>
                            by
                            <#if article.articleAuthorURL != "">
                            <a title="${article.articleAuthorName}" href="http://${article.articleAuthorURL}" target="_blank">
                                ${article.articleAuthorName}</a>
                            <#else>
                            ${article.articleAuthorName}
                            </#if>
                            <span class="right">
                                <span class="date-icon" title="${createDateLabel}"></span>
                                <span class="left">&nbsp;${article.articleCreateDate?string('yyyy-MM-dd HH:mm')}</span>
                            </span>
                            <span class="clear"></span>
                        </div>
                        <div>
                            <#list article.articleTags?split(',') as tagTitle>
                            <h3 title="${tagTitle}">
                                <a href="/tags/${tagTitle}">${tagTitle}</a>
                            </h3>
                            </#list>
                            <span class="right">
                                <#if "1970" != article.articleLastCmtDate?string('yyyy')>
                                <a href="/entries/${article.oId}#comments">
                                    <span class="comment-icon" title="${commentCountLabel}"></span>
                                    <span class="left">&nbsp;${article.articleCommentCount}</span>
                                </a>
                                <span class="left">
                                    <span class="left">&nbsp;|&nbsp;</span>
                                    <span class="last-date-icon" title="${lastCommentDateLabel}"></span>
                                    <span class="left">&nbsp;${article.articleLastCmtDate?string('yyyy-MM-dd HH:mm')}</span>
                                </span>
                                <#else>
                                <span class="sofa-icon" title="${sofaLabel}"></span>&nbsp;<a href="/entries/${article.oId}#commentContent">${sofaLabel}</a>
                                </#if>
                            </span>
                        </div>
                    </div>
                    <div class="clear"></div>
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
