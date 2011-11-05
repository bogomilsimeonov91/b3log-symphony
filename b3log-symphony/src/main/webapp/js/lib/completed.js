/*
 * Copyright (C) 2010, Liyuan Li
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
(function($){var j=new Date().getTime();var k='completed';var l=function(){this._defaults={"styleClass":{"panelClass":"completed-panel","inputClass":"completed-input","ckClass":"completed-ck"},"separator":","},this._settingsDataFormat={}};$.extend(l.prototype,{_attach:function(a,b){if(!a.id){this.uuid++;a.id='dp'+this.uuid}var c=this._newInst($(a));c.settings=$.extend({"buttonText":"\u9009\u62e9"},b||{});$.data(a,k,c);this._init(a)},_newInst:function(a){var b=a[0].id.replace(/([^A-Za-z0-9_])/g,'\\\\$1');return{"id":b}},_getInst:function(a){try{return $.data(a,k)}catch(err){throw'Missing instance data for this completed';}},_destroyCompleted:function(){},_init:function(b){var c=this._getInst(b);var d=c.id,settings=c.settings;this._buildHTML(d,settings);$(document).click(function(a){if(a.target.id!==d){$("#"+d+"SelectedPanel").hide()}});this._buildCheckboxPanel(d,settings.data)},_buildHTML:function(b,c){var d=c.height+"px",classStyle=this._getDefaults($.completed._defaults,c,"styleClass");var e=$("#"+b);var f="<button onclick=\"$('#"+b+"CheckboxPanel').toggle()\">"+c.buttonText+"</button><div id='"+b+"SelectedPanel' class='"+classStyle.panelClass+"' style='height:"+d+";'></div><div class='none "+classStyle.ckClass+"' id='"+b+"CheckboxPanel'><div>";c.data.sort();e.after(f).bind("keyup",{settings:c},this._keyupAction).bind("keydown",function(a){c.chinese=a.keyCode}).addClass(classStyle.inputClass).width(e.width()-66);var g=$("#"+b+"SelectedPanel");c.tipNum=0;g.width(e.width()+2)},_keyupAction:function(a){var b=a.data.settings,currentWordObj=$.completed._getCurrentWord(this,b);if(currentWordObj.currentWord===""||a.keyCode===27||a.keyCode===16||a.keyCode===16){$("#"+this.id+"SelectedPanel").hide();b.tipNum=0;return}var c=$.completed._getMatchData(b.data,this.value,currentWordObj.currentWord);if(a.keyCode===38){if(b.tipNum>0){b.tipNum--}else{b.tipNum=c.length-1}}if(a.keyCode===40){if(b.tipNum<c.length-1){b.tipNum++}else{b.tipNum=0}}$.completed._buildSelectedPanel(this.id,c,b,currentWordObj.currentWord);if(a.keyCode===13&&c[b.tipNum]&&b.chinese!==229){var d=this.value;this.value=d.substring(0,currentWordObj.startPos)+c[b.tipNum]+d.substring(currentWordObj.endPos,d.length);$("#"+this.id+"SelectedPanel").hide();b.chinese=undefined}if(a.keyCode!==38&&a.keyCode!==40){b.tipNum=0}},_getCurrentWord:function(a,b){var c=$(a).val(),tag=true,endPos=0,startPos=0,separator=$.completed._defaults.separator;if(c===""){return{currentWord:"",startPos:startPos,endPos:endPos}}if(document.selection){try{var d=document.selection.createRange();var f=a.createTextRange();f.collapse(true);f.select();var g=document.selection.createRange();g.setEndPoint("EndToEnd",d);b.curPos=g.text.length;d.select()}catch(e){delete e}}else{b.curPos=a.selectionStart}var h=b.curPos;for(var i=0;i<c.length;i++){if(c.charAt(i)===separator){if(i>=h&&tag){endPos=i;tag=false}}}if(tag===true){tag=false;endPos=c.length}for(var j=endPos;j>-1;j--){if(c.charAt(j)===separator){if(j<h&&!tag){startPos=j+1;tag=true}}}return{currentWord:c.substring(startPos,endPos),startPos:startPos,endPos:endPos}},_getMatchData:function(a,b,c){var d=b.split($.completed._defaults.separator);var e=[];for(var i=0;i<a.length;i++){if(typeof a[i]==="number"){a[i]=a[i].toString()}if(a[i].toLowerCase().indexOf(c.toLowerCase())>-1){var f=true;for(var k=0;k<d.length;k++){if(a[i]===d[k].toString()&&a[i].toLowerCase()!==c.toLowerCase()){f=false}}if(f){e.push(a[i])}}}return e},_mousemoveSelectPanel:function(a,i,b){$(a).parent().find("a").removeClass("selected");a.className='selected';var c=$.completed._getInst(document.getElementById(b));c.settings.tipNum=i},_buildSelectedPanel:function(e,f,g,h){var j=$("#"+e+"SelectedPanel");if(f.length===0){j.html("").hide();return}if(g.tipNum>=f.length){g.tipNum=0}var k="";for(var i=0;i<f.length;i++){var l="",highlightHTML=f[i].replace(h,"<b>"+h+"</b>");if(g.tipNum===i){l="class='selected'"}k+="<a href='javascript:void(0);'                                         onmousemove=\"$.completed._mousemoveSelectPanel(this, "+i+", '"+e+"');\"                                        "+l+">"+highlightHTML+"</a>"}j.html(k).show();var m=$("#"+e+"SelectedPanel a.selected");if(m.position().top+j.scrollTop()>50-m.height()){j.scrollTop(m.position().top+j.scrollTop()+m.height()-50)}if(m.position().top<0){j.scrollTop(j.scrollTop-m.height())}$("#"+e+"SelectedPanel a").click(function(){var a=document.getElementById(e);var b=$.completed._getCurrentWord(document.getElementById(e),g);var c=$.completed._getMatchData(g.data,a.value,b.currentWord);var d=a.value;a.value=d.substring(0,b.startPos)+c[g.tipNum]+d.substring(b.endPos,d.length);g.tipNum=0;$(a).focus()})},_buildCheckboxPanel:function(c,d){var e="",$input=$("#"+c);for(var i=0;i<d.length;i++){e+="<span>"+d[i]+"</span>"}$("#"+c+"CheckboxPanel").html(e+"<div class='clear'></div>");$("#"+c+"CheckboxPanel"+" span").click(function(){var a=$input.val(),currentVal=this.innerHTML;if(this.className==="selected"){this.className="";var b=a.substr(a.indexOf(currentVal)+currentVal.length,1);if(currentVal===a||b!==","){$input.val(a.replace(currentVal,""))}else{$input.val(a.replace(currentVal+",",""))}}else{this.className="selected";if(a.replace(/\s/g,"")===""||a.substr(a.length-1,1)===","){$input.val(a+currentVal)}else{$input.val(a+","+currentVal)}}});this._matchChecked(c);$input.blur(function(){$.completed._matchChecked(c)})},_matchChecked:function(a){var b=$("#"+a).val().split(",");$("#"+a+"CheckboxPanel span").removeClass().each(function(){for(var i=0;i<b.length;i++){if(this.innerHTML===b[i]){this.className="selected"}}})},_getDefaults:function(a,b,c){if(c==="styleClass"){if(b.theme==="default"||b.theme===undefined){return a.styleClass}b.styleClass={};for(var d in a[c]){b.styleClass[d]=b.theme+"-"+a.styleClass[d]}}else if(c==="height"||c==="width"){if(b[c]===null||b[c]===undefined){return"auto"}else{return b[c]+"px"}}else{if(b[c]===null||b[c]===undefined){return a[c]}}return b[c]}});$.fn.completed=function(a){var b=Array.prototype.slice.call(arguments);return this.each(function(){typeof a=='string'?$.completed['_'+a+'Completed'].apply($.completed,[this].concat(b)):$.completed._attach(this,a)})};$.completed=new l();window['DP_jQuery_'+j]=$})(jQuery);