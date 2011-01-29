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
            <button onclick="testAddArticle()">Test Add Article</button>
            <#list tags as tag>
            <dl>
                <dd>
                    <span>
                        ${tag.tagTitle}
                    </span>
                    <span>
                        <a href="#">author1</a>,
                        <a href="#">author2</a>,
                        <a href="#">author3</a>,
                        <a href="#">author4</a>,
                        <a href="#">author5</a>
                    </span>
                    <span>
                        ${tag.tagReferenceCount}/ ${tag.tagCommentCount}
                    </span>
                </dd>
                <#list tag.tagArticles as article>
                <dd>
                    <span>
                        icon
                    </span>
                    <span>
                        ${article.articleTitle}
                    </span>
                    <span>
                        ${article.articleTags}
                    </span>
                    <span>
                        ${article.articleCommentCount}
                    </span>
                    <span>
                        ${article.articleAuthorName}
                    </span>
                    <span>
                        ${article.articleCreateDate?string('yyyy-MM-dd HH:mm:ss')}/last comment date
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
            var util = new Util();
            util.initLogin();
            function testAddArticle() {
                var requestJSONObject = {
                    "article": {
                        "articleTitle": "title",
                        "articlePermalink": "/permalink",
                        "articleTags": "tag1, tag2, ....",
                        "articleAuthorEmail": "DL88250@gmail.com",
                        "articleContent": "content"
                    },
                    "blogTitle": "testBlogTitle",
                    "blogHost": "test.com",
                    "soloVersion": "0.2.5"
                };

                $.ajax({
                    url: "/add-article",
                    type: "POST",
                    data: JSON.stringify(requestJSONObject),
                    success: function(result, textStatus){
                        alert(JSON.stringify(result));
                    }
                });
            }
        </script>
    </body>
</html>
