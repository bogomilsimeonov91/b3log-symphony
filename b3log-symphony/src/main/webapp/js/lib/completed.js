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

(function ($) {
    $.fn.extend({
        completed: {
            version: "0.0.0.6",
            author: "lly219@gmail.com"
        }
    });

    var dpuuid = new Date().getTime();
    var PROP_NAME = 'completed';

    var Completed = function () {
        this._defaults = {
            "styleClass": {
                "panelClass": "completed-panel"
            },
            separator: ","
        },
        this._settingsDataFormat = {
            
        }
    };

    $.extend(Completed.prototype, {
        _attach: function (target, settings) {
            if (!target.id) {
                this.uuid++;
                target.id = 'dp' + this.uuid;
            }
            var inst = this._newInst($(target));

            inst.settings = $.extend({}, settings || {});
            $.data(target, PROP_NAME, inst);
            this._init(target);
        },

        /* Create a new instance object. */
        _newInst: function (target) {
            // escape jQuery meta chars
            var id = target[0].id.replace(/([^A-Za-z0-9_])/g, '\\\\$1');
            return {
                id: id
            };
        },

        _getInst: function (target) {
            try {
                return $.data(target, PROP_NAME);
            } catch (err) {
                throw 'Missing instance data for this completed';
            }
        },

        _destroyCompleted: function () {

        },

        _init: function (target) {
            var inst = this._getInst(target);
            var id = inst.id,
            settings = inst.settings,
            height = '';
            var $it = $("#" + id);
            if (settings.height) {
                height = settings.height + "px";
            } else {
                height = "auto"
            }
            var panelHTML = "<div id='" + id + "Panel' class='"
            + $.completed._getDefaults($.completed._defaults, settings, "styleClass").panelClass
            + "' style='height:" + height + ";'></div>";

            settings.data.sort();

            $it.after(panelHTML).bind("keyup", {
                settings: settings
            }, this._keyupAction).click(function (event) {
                event.target.selectionStart = settings.selectionEnd;
                event.target.selectionEnd = settings.selectionEnd;
            });

            var currentStringArray = $it.val().split($.completed._defaults.separator);
            settings.currentName = currentStringArray[currentStringArray.length - 1];
            settings.selectionEnd = $it.val().length;
            settings.currentNum = -1;

            var $panel = $("#" + id + "Panel");
            $panel.width($it.width() - parseInt($panel.css("padding-top"))
                - parseInt($panel.css("padding-bottom")));

            $it.blur(function () {
                $panel.hide();
            });
        },

        _getMatchData: function (subString, data) {
            var matchData = [];
            for (var i = 0; i < data.length; i++) {
                if (typeof data[i] === "number") {
                    data[i] = data[i].toString();
                }
                if (data[i].toLowerCase().indexOf(subString.toLowerCase()) === 0) {
                    matchData.push(data[i]);
                }
            }
            return matchData;
        },

        _removeExisted: function (existedArray, datas) {
            for (var i = 0; i < existedArray.length; i++) {
                for (var index in datas) {
                    if (datas[index] === existedArray[i]) {
                        datas.splice(index, 1);
                    }
                }
            }
        },

        _setCurrentName: function (currentName, event, selectionEnd) {
            var currentValue = event.currentTarget.value;
            if (event.keyCode === 46 || event.keyCode === 38 || event.keyCode === 40
                || event.keyCode === 45 || event.keyCode === 16 ||
                event.keyCode === 17 || event.keyCode === 18
                ||  event.keyCode === 20 || event.keyCode === 93 || event.keyCode === 13) {
            // delete || top || bottom || insert || shift || ctrl || alt || capslk || mouse right || enter
            } else if (event.keyCode === 35 || event.keyCode === 36
                || event.keyCode === 34 || event.keyCode === 33
                || event.keyCode === 37 || event.keyCode === 39) {
                // end || home || pgDn || pgUp || left || right
                event.target.selectionStart = selectionEnd;
                event.target.selectionEnd = selectionEnd;
            } else {
                currentName = currentValue.split($.completed._defaults.separator).pop();
            }
            return currentName;
        },

        _keyupAction: function (event) {
            var settings = event.data.settings,
            currentValue = this.value,
            id = this.id;
            var currentArray = currentValue.split($.completed._defaults.separator);
            settings.currentName = $.completed._setCurrentName(settings.currentName, event, settings.selectionEnd);
            currentArray.pop();
            var matchData = $.completed._getMatchData(settings.currentName, settings.data);
            $.completed._removeExisted(currentArray, matchData);
            settings.selectionEnd = event.currentTarget.selectionEnd;
            if (matchData.length > 0) {
                if (event.keyCode === 38 || event.keyCode === 40) {
                    if (event.keyCode === 38) { // up
                        if (settings.currentNum > -1) {
                            settings.currentNum--;
                        }
                    } else { // down
                        if (settings.currentNum < matchData.length - 1) {
                            settings.currentNum++;
                        }
                    }
                    if (settings.currentNum > -1) {
                        if (currentArray.length === 0) {
                            this.value = matchData[settings.currentNum];
                        } else {
                            this.value = currentArray.toString() + $.completed._defaults.separator + matchData[settings.currentNum];
                        }
                    }
                } else if (event.keyCode === 13) {
                    // enter
                    settings.currentNum = -1;
                    $.completed._enterAction(id, settings, event.currentTarget.selectionEnd);
                    return;
                }
                $.completed._buildPanel(id, matchData, settings.currentNum);
            } else {
                settings.currentNum = -1;
                $("#" + id + "Panel").hide();
            }
        },

        _enterAction: function (id, settings, selectionEnd) {
            settings.currentName = "";
            settings.currentNum = -1;
            settings.selectionEnd = selectionEnd + 1;
            var value = $("#" + id).val();
            if (value.charAt(value.length - 1) !== $.completed._defaults.separator) {
                $("#" + id).val($("#" + id).val() + $.completed._defaults.separator);
            }
            $("#" + id + "Panel").hide();
        },

        _buildPanel: function (id, data, currentNum) {
            var $panel = $("#" + id + "Panel");
            var panelHTML = "";
            for (var i = 0; i < data.length; i++){
                if (currentNum === i) {
                    panelHTML += "<div class='selected'>" + data[i] + "</div>";
                } else {
                    panelHTML += "<div>" + data[i] + "</div>";
                }
            }
            $panel.html(panelHTML).show();

            var $selected = $("#" + id + "Panel div.selected");
            if ($selected.length > 0) {
                if ($selected.position().top + $panel.scrollTop() > 50 - $selected.height()) {
                    $panel.scrollTop($selected.position().top + $panel.scrollTop() + $selected.height() - 50);
                }
                if ($selected.position().top < 0) {
                    $panel.scrollTop($panel.scrollTop - $selected.height());
                }
            }
        },
        
        _getDefaults: function (defaults, settings, key) {
            if (key === "styleClass") {
                if (settings.theme === "default" || settings.theme === undefined) {
                    return defaults.styleClass;
                }

                settings.styleClass = {};
                for (var styleName in defaults[key]) {
                    settings.styleClass[styleName] = settings.theme + "-" + defaults.styleClass[styleName];
                }
            } else if ((key === "height" && settings[key] !== "auto") || key === "width") {
                if (settings[key] === null || settings[key] === undefined) {
                    return defaults[key] + "px";
                } else {
                    return settings[key] + "px";
                }
            } else {
                if (settings[key] === null || settings[key] === undefined) {
                    return defaults[key];
                }
            }
            return settings[key];
        }
    });

    $.fn.completed = function (options) {
        var otherArgs = Array.prototype.slice.call(arguments);
        return this.each(function () {
            typeof options == 'string' ?
            $.completed['_' + options + 'Completed'].
            apply($.completed, [this].concat(otherArgs)) :
            $.completed._attach(this, options);
        });
    };

    $.completed = new Completed();

    // Add another global to avoid noConflict issues with inline event handlers
    window['DP_jQuery_' + dpuuid] = $;
})(jQuery);
