<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${userName} - ${titleIndex}">
        <meta name="keywords" content="${userName},${titleIndex}"/>
        <meta name="description" content="${metaDescription}"/>
        </@head>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="symphony-content user">
            <div class="marginBottom20">
                <a href="/users/${userName}">
                    <img alt="${userName}" title="${userName}"
                         src="${userThumbnailURL}" class="big-head-img left"/>
                </a>
                <div class="left">
                    <h1 class="bigger-font" title="http://${userURL}">
                        <a href="http://${userURL}">${userName}</a>
                    </h1>
                    <br/><br/>
                    <h4 class="middle-font">
                        ${sign}
                    </h4>
                </div>
                <span class="clear"></span>
            </div>
            <h2 class="caption">${lastEntryLabel}</h2>
            <dl class="entry-list-top marginBottom20">
                <#list articles as article>
                <#if article_index % 2 == 0>
                <dd class="even">
                    <#else>
                <dd class="odd">
                    </#if>
                    <h3 title="${article.articleTitle}">
                        <a href="/entries/${article.oId}" class="big-font">${article.articleTitle}</a>
                        &nbsp;&nbsp;&nbsp;
                    </h3>
                    <#list article.articleTags?split(',') as tagTitle>
                    <h4 title="${tagTitle}" class="middle-font">
                        <a href="/tags/${tagTitle?url('UTF-8')}">${tagTitle}</a><#if tagTitle_has_next>,</#if>
                    </h4>
                    </#list>
                    <span class="right">
                        <#if "1970" != article.articleLastCmtDate?string('yyyy')>
                        <a href="/entries/${article.oId}#comments"
                           title="${lastCommentDateLabel}：${article.articleLastCmtDate?string('yyyy-MM-dd HH:mm')}">
                            <span class="comment-icon"></span>
                            <span class="left">&nbsp;${article.articleCommentCount}</span>
                        </a>
                        <#else>
                        <a href="/entries/${article.oId}#commentContent">
                            <span class="sofa-icon" title="${sofaLabel}"></span>
                        </a>
                        </#if>
                        <span class="left small-font">
                            &nbsp;&nbsp;&nbsp;${article.articleCreateDate?string('yyyy-MM-dd HH:mm:ss')}
                        </span>
                    </span>
                    <span class="clear"></span>
                    </#list>
                </dd>
            </dl>
            <h2 class="caption">
                ${lastCommentLabel}
            </h2>
            <dl class="entry-list-top">
                <#list comments as comment>
                <#if comment_index % 2 == 0>
                <dd class="even">
                    <#else>
                <dd class="odd">
                    </#if>
                    <div class="left">
                        <a href="/entries/${comment.commentEntryId}" class="big-font">
                            ${comment.commentEntryTitle}
                        </a>
                    </div>
                    <div class="right">
                        <a href="/entries/${comment.commentEntryId}#${comment.oId}comment">
                            <span class="comment-icon"></span>
                        </a>
                        <span class="small-font left">
                            &nbsp;&nbsp;&nbsp;${comment.commentDate?string('yyyy-MM-dd HH:mm:ss')}
                        </span>
                    </div>
                    <span class="clear"></span>
                    <div class="article-body">
                        ${comment.commentContent}
                    </div>
                </dd>
                </#list>
            </dl>
        </div>
        <#include "footer.ftl"/>
        <script type="text/javascript">
            Util.initStatus();
            Util.initPagination();
        </script>
    </body>
</html>
