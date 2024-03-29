<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${tag.tagTitle} - ${titleIndex}">
        <meta name="keywords" content="${tag.tagTitle},${titleIndex}"/>
        <meta name="description" content="${metaDescription}"/>
        </@head>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="symphony-content tag">
            <#include "header.ftl"/>
            <div class="title">
                <h1 class="left">
                    <a title="${tag.tagTitle}" href="/tags/${tag.tagTitle?url('UTF-8')}" class="bigger-font">
                        ${tag.tagTitle}</a>
                </h1>
                <div class="count">
                    <span class="tag-icon" title="${tagRefCountLabel}"></span>&nbsp;
                    <span class="left">&nbsp;${tag.tagReferenceCount}&nbsp;&nbsp;</span>
                    <span class="comment-icon" title="${commentCountLabel}"></span>
                    <span>${tag.tagCommentCount}</span>
                </div>
                <div class="right">
                    <#list tagTopUsers as topAuthor>
                    <a href="/users/${topAuthor.userName}">
                        <img class="small-head-img" alt="${topAuthor.userName}" title="${topAuthor.userName}"
                             src="${topAuthor.userThumbnailURL}"/>
                    </a>
                    </#list>
                </div>
                <span class="clear"></span>
            </div>
            <dl class="entry-list">
                <#list articles as article>
                <dd>
                    <div class="left">
                        <a href="/users/${article.articleAuthorName}">
                            <img class="middle-head-img" src="${article.articleAuthorThumbnailURL}"
                                 title="${article.articleAuthorName}" alt="${article.articleAuthorName}"/>
                        </a>
                    </div>
                    <div class="left title-tag">
                        <h2 title="${article.articleTitle}">
                            <a href="/entries/${article.oId}" class="big-font">
                                ${article.articleTitle}
                            </a>
                        </h2>
                        by
                        <#if article.articleAuthorURL != "">
                        <a href="http://${article.articleAuthorURL}" target="_blank">${article.articleAuthorName}</a>
                        <#else>
                        ${article.articleAuthorName}
                        </#if>
                        <span class="right">
                            <span class="left small-font">
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
                        <#list article.articleTags?split(',') as tagTitle>
                        <h3 title="${tagTitle}" class="middle-font">
                            <a href="/tags/${tagTitle}">${tagTitle}</a><#if tagTitle_has_next>,</#if>
                        </h3>
                        </#list>
                    </div>
                    <div class="clear"></div>
                </dd>
                </#list>
            </dl>
            <#if paginationPageNums?size != 0>
            <div id="pagination">
                <#if paginationPageNums?first != 1>
                <a href="/tags/${tag.tagTitle?url('UTF-8')}?p=1" title="${firstPageLabel}"><<</a>
                <a id="previousPage"
                   href="/tags/${tag.tagTitle?url('UTF-8')}?p={paginationPageCount}"
                   title="${previousPageLabel}"><</a>
                </#if>
                <#list paginationPageNums as page>
                <a href="/tags/${tag.tagTitle?url('UTF-8')}?p=${page}" title="${page}">${page}</a>
                </#list>
                <#if paginationPageNums?last!=paginationPageCount>
                <a id="nextPage"
                   href="/tags/${tag.tagTitle?url('UTF-8')}?p={paginationPageCount}"
                   title="${nextPagePabel}">></a>
                <a href="/tags/${tag.tagTitle?url('UTF-8')}?p=${paginationPageCount}"
                   title="${lastPageLabel}">>></a>
                </#if>
                ${sumLabel} ${paginationPageCount} ${pageLabel}
            </div>
            </#if>
        </div>
        <#include "footer.ftl"/>
        <script type="text/javascript">
            Util.initStatus();
            Util.initPagination();
        </script>
    </body>
</html>
