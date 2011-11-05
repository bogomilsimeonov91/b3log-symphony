<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${ChinasbLabel} - ${titleIndex}"></@head>
    </head>
    <body>
        <#include "top.ftl"/>
        <div class="symphony-content">
            <#include "header.ftl"/>
            <dl class="entry-list">
                <#list articles as article>
                <dd>
                    <div class="left">
                        <a title="${article.articleAuthorName}" href="/users/${article.articleAuthorName}" >
                            <img class="middle-head-img" src="${article.articleAuthorThumbnailURL}"
                                 title="${article.articleAuthorName}" alt="${article.articleAuthorName}"/>
                        </a>
                    </div>
                    <div class="left title-tag">
                        <div>
                            <h3 title="${article.articleTitle}">
                                <a href="/entries/${article.oId}" class="big-font">
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
                        </div>
                        <#list article.articleTags?split(',') as tagTitle>
                        <h4 title="${tagTitle}" class="middle-font">
                            <a href="/tags/${tagTitle?url('UTF-8')}">${tagTitle}</a><#if tagTitle_has_next>,</#if>
                        </h4>
                        </#list>
                    </div>
                    <div class="clear"></div>
                </dd>
                </#list>
            </dl>
            <#if paginationPageNums?size != 0>
            <div id="pagination">
                <#if paginationPageNums?first != 1>
                <a href="/?p=1" title="${firstPageLabel}"><<</a>
                <a id="previousPage"
                   href="/?p={paginationPageCount}"
                   title="${previousPageLabel}"><</a>
                </#if>
                <#list paginationPageNums as page>
                <a href="/?p=${page}" title="${page}">${page}</a>
                </#list>
                <#if paginationPageNums?last != paginationPageCount>
                <a id="nextPage"
                   href="/?p={paginationPageCount}"
                   title="${nextPagePabel}">></a>
                <a href="/?p=${paginationPageCount}"
                   title="${lastPageLabel}">>></a>
                </#if>
                ${sumLabel} ${paginationPageCount} ${pageLabel}
            </div>
            </#if>
        </div>
        <#include "footer.ftl"/>
        <script type="text/javascript">
            Util.initStatus();
        </script>
    </body>
</html>
