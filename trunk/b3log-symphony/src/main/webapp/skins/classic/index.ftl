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
        <div class="content index">
            <#list tags as tag>
            <div class="title">
                <h2 class="left">
                    <a title="${tag.tagTitle}" href="/tags/${tag.tagTitle?url('UTF-8')}">
                        ${tag.tagTitle}
                    </a>
                </h2>
                <div class="count">
                    <span class="tag-icon" title="${tagRefCountLabel}"></span>
                    <span class="left">&nbsp;${tag.tagReferenceCount}&nbsp;|&nbsp;</span>
                    <span class="comment-icon" title="${commentCountLabel}"></span>
                    <span class="left">&nbsp;${tag.tagCommentCount}</span>
                </div>
                <div class="right">
                    <#list tag.tagTopAuthors as topAuthor>
                    <#if topAuthor.userURL != "">
                    <a href="http://${topAuthor.userURL}" target="_blank">
                        <img class="small-head-img" alt="${topAuthor.userName}" title="${topAuthor.userName}"
                             src="${topAuthor.userThumbnailURL}"/>
                    </a>
                    <#else>
                    <img class="small-head-img" alt="${topAuthor.userName}" title="${topAuthor.userName}"
                         src="${topAuthor.userThumbnailURL}"/>
                    </#if>
                    </#list>
                </div>
                <span class="clear"></span>
            </div>
            <dl>
                <#list tag.tagArticles as article>
                <dd>
                    <div class="left">
                        <#if article.articleAuthorURL != "">
                        <a title="${article.articleAuthorName}" href="http://${article.articleAuthorURL}" target="_blank">
                            <img class="middle-head-img" src="${article.articleAuthorThumbnailURL}"
                                 title="${article.articleAuthorName}" alt="${article.articleAuthorName}"/>
                        </a>
                        <#else>
                        <img class="middle-head-img" src="${article.articleAuthorThumbnailURL}"
                             alt="${article.articleAuthorName}" title="${article.articleAuthorName}"/>
                        </#if>
                    </div>
                    <div class="left main">
                        <div>
                            <h3 title="${article.articleTitle}">
                                <a href="/entries/${article.oId}">
                                    ${article.articleTitle}</a>
                            </h3>
                            by
                            <#if article.articleAuthorURL != "">
                            <a title="${article.articleAuthorName}" href="http://${article.articleAuthorURL}" target="_blank">
                                ${article.articleAuthorName}</a>
                            <#else>
                            ${article.articleAuthorName}
                            </#if>
                            <span class="right">
                                <span class="left">
                                    ${article.articleCreateDate?string('yyyy-MM-dd HH:mm')}&nbsp;
                                </span>
                                <#if "1970" != article.articleLastCmtDate?string('yyyy')>
                                <a href="/entries/${article.oId}#comments"
                                   title="${lastCommentDateLabel}：${article.articleLastCmtDate?string('yyyy-MM-dd HH:mm')}">
                                    <span class="comment-icon"></span>
                                    <span class="left">&nbsp;${article.articleCommentCount}</span>
                                </a>
                                <#else>
                                <span class="left">&nbsp;&nbsp;</span>
                                <a href="/entries/${article.oId}#commentContent">
                                    <span class="sofa-icon" title="${sofaLabel}"></span>
                                </a>
                                <span class="left">&nbsp;</span>
                                </#if>
                            </span>
                            <span class="clear"></span>
                        </div>
                        <#list article.articleTags?split(',') as tagTitle>
                        <h4 title="${tagTitle}">
                            <a href="/tags/${tagTitle?url('UTF-8')}">${tagTitle}</a><#if tagTitle_has_next>,</#if>
                        </h4>
                        </#list>
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
                    "adminConsoleLabel": "${adminConsoleLabel}",
                    "postEntryLabel": "${postEntryLabel}"
                }
            });
            index.initStatus();
        </script>
    </body>
</html>
