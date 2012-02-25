/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @fileoverview b3log index js.
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.1, Jan 9, 2012
 */
var Index = {
    getNews: function () {
        $.ajax({
            url: "http://symphony.b3log.org:80/get-news",
            type: "GET",
            dataType:"jsonp",
            jsonp: "callback",
            error: function(){
                $("#news").html("加载新闻列表失败 :-(").css("background-image", "none");
            },
            success: function(data, textStatus){
                var articles = data.articles;
                if (0 === articles.length) {
                    $("#news").html("无新闻").css("background-image", "none");
                    return;
                }
                
                var listHTML = "<ul>";
                for (var i = 0; i < articles.length && i < 9; i++) {
                    var article = articles[i];
                    var articleLiHtml = "<li>"
                    + "<span>[" + Index._getDate(article.articleCreateDate) 
                    + "]</span><a target='_blank' href='" + article.articlePermalink + "'>"
                    +  article.articleTitle + "</a></li>"
                    listHTML += articleLiHtml
                }
                listHTML += "</ul>";
                    
                $("#news").html(listHTML).css("background-image", "none");
            }
        });
    },
    
    _getDate: function (a) {
        var c=new Date(a);
        var d=c.getFullYear().toString(),
        e=c.getMonth()+1,
        f=c.getDate();
        
        if (e < 10) {
            e = "0" + e;
        }
        
        if (f < 10) {
            f = "0" + f;
        }
        return d + "-" + e + "-" + f;
    },
    
    initThemes: function () {
        Index._initThemesHTML();
        
        var $themes = $("#themes");
        
        $themes.find("div").mouseover(function () {
            var $it = $(this);
            if ($it.find("span").css("opacity") === "0" || $it.hasClass("center")) {
                return;    
            }
            
            var imageArray = $it.find("img").attr("src").split("/");
            var image = imageArray[imageArray.length - 1];
            
            $themes.find("span").css("opacity", "0.7");
            $it.find("span").css("opacity", "0");
            
            $("#themesCenter>div").hide();
            $("#themes" + image.substring(0, image.length - 4)).show();
        });
    },
    
    _initThemesHTML: function () {
        var authors = ['Dongxu Wang', 'Vanessa', 'Vanessa', 'Vanessa', 'Lamb', 'Vanessa',
        'Vanessa', 'Vanessa', 'Dongxu Wang', 'Ansen', 'Ansen', 'Noday', 'Noday'],
        authorUrls = ['dx.b3log.org', 'vanessa.b3log.org', 'vanessa.b3log.org', 
        'vanessa.b3log.org', 'lamb.b3log.org', 'vanessa.b3log.org',
        'vanessa.b3log.org', 'vanessa.b3log.org', 'dx.b3log.org', 'www.ansen.org',
        'www.ansen.me', 'www.noday.net', 'www.noday.net'],
        downloads = [],
        images = ['mobile', 'andrea', 'classic', 'community', 'favourite', 'tree-house',
        'i-nove', 'neoease', 'owmx', 'shawn', 'coda', '5stylesm', 'idream'],
        centerHTML = "",
        className = "";
        
        for (var j = 0; j < 9; j++) {
            downloads[j] = 'code.google.com/p/b3log-solo/downloads/list';
        //'code.google.com/p/b3log-solo/downloads/list?can=1&q=%E7%9A%AE%E8%82%A4&colspec=Filename+Summary+Uploaded+Size+DownloadCount+UploadedBy';
        }
        
        downloads[0] = 'code.google.com/p/b3log-solo/downloads/list';
        downloads[2] = 'code.google.com/p/b3log-solo/downloads/list';
        downloads[9] = 'code.google.com/p/blogskins/downloads/list';
        downloads[10] = 'code.google.com/p/blogskins/downloads/list';
        downloads[11] = 'code.google.com/p/noday/downloads/list';
        downloads[12] = 'code.google.com/p/noday/downloads/list';
        
        for (var i = 0; i < images.length; i++) {
            centerHTML += '<div class="center ' + className + '" id="themes' + images[i] 
            + '"><img src="images/themes/' + images[i] + '.png"/>'
            + '<span class="info"><a href="http://' + authorUrls[i] + '" target="_blank">' + authors[i] + '</a><br/>'
            + '<a href="http://' + downloads[i] + '" target="_blank">Download</a>'
            + '</span></div>';
        
            if (i === 0) {
                className = "none";
            }
        }
        
        $("#themesCenter").html(centerHTML);
    }
    
};

(function () {
    Index.getNews();
    Index.initThemes();
    $("#nav").scrollv();
})();