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
                $("#news").html("Loading B3log Announcement failed :-(").css("background-image", "none");
            },
            success: function(data, textStatus){
                var articles = data.articles;
                if (0 === articles.length) {
                    return;
                }
            
                var listHTML = "<ul>";
                for (var i = 0; i < articles.length; i++) {
                    var article = articles[i];
                    var articleLiHtml = "<li>"
                    + "<a target='_blank' href='" + article.articlePermalink + "'>"
                    +  article.articleTitle + "</a>&nbsp; <span class='date'>" + article.articleCreateDate;
                    + "</span></li>"
                    listHTML += articleLiHtml
                }
                listHTML += "</ul>";
                    
                $("#news").html(listHTML).css("background-image", "none");
            }
        });
    },
    
    initThemes: function () {
        var $themes = $("#themes");
        
        $themes.find("div").mouseover(function () {
            var $it = $(this);
            if ($it.find("span").css("opacity") === "0" || $it.hasClass("center")) {
                return;    
            }
            
            $themes.find("span").css("opacity", "0.5");
            $it.find("span").css("opacity", "0");
            
            $("#themesCenter>div").hide();
            $("#themes" + $it.data("theme")).show();
        });
    }
};

(function () {
    // Index.getNews();
    // Index.initThemes();
    $("#nav").scrollv();
})();