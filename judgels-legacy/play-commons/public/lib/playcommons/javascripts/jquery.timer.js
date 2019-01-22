jQuery.fn.extend({
    everyTime: function (a, b, c, d) {
        return this.each(function () {
            jQuery.timer.add(this, a, b, c, d);
        });
    }, oneTime: function (a, b, c) {
        return this.each(function () {
            jQuery.timer.add(this, a, b, c, 1);
        });
    }, stopTime: function (a, b) {
        return this.each(function () {
            jQuery.timer.remove(this, a, b);
        });
    }
});
jQuery.extend({
    timer: {
        global: [],
        guid: 1,
        dataKey: "jQuery.timer",
        regex: /^([0-9]+(?:\.[0-9]*)?)\s*(.*s)?$/,
        powers: {ms: 1, cs: 10, ds: 100, s: 1000, das: 10000, hs: 100000, ks: 1000000},
        timeParse: function (c) {
            if (c === undefined || c === null) {
                return null;
            }
            var a = this.regex.exec(jQuery.trim(c.toString()));
            if (a[2]) {
                var b = parseFloat(a[1]);
                var d = this.powers[a[2]] || 1;
                return b * d;
            } else {
                return c;
            }
        },
        add: function (d, b, c, f, h) {
            var a = 0;
            if (jQuery.isFunction(c)) {
                if (!h) {
                    h = f;
                }
                f = c;
                c = b;
            }
            b = jQuery.timer.timeParse(b);
            if (typeof b != "number" || isNaN(b) || b < 0) {
                return;
            }
            if (typeof h != "number" || isNaN(h) || h < 0) {
                h = 0;
            }
            h = h || 0;
            var g = jQuery.data(d, this.dataKey) || jQuery.data(d, this.dataKey, {});
            if (!g[c]) {
                g[c] = {};
            }
            f.timerID = f.timerID || this.guid++;
            var e = function () {
                if ((++a > h && h !== 0) || f.call(d, a) === false) {
                    jQuery.timer.remove(d, c, f);
                }
            };
            e.timerID = f.timerID;
            if (!g[c][f.timerID]) {
                g[c][f.timerID] = window.setInterval(e, b);
            }
            this.global.push(d);
        },
        remove: function (c, b, d) {
            var e = jQuery.data(c, this.dataKey), a;
            if (e) {
                if (!b) {
                    for (b in e) {
                        this.remove(c, b, d);
                    }
                } else {
                    if (e[b]) {
                        if (d) {
                            if (d.timerID) {
                                window.clearInterval(e[b][d.timerID]);
                                delete e[b][d.timerID];
                            }
                        } else {
                            for (var f in e[b]) {
                                window.clearInterval(e[b][f]);
                                delete e[b][f];
                            }
                        }
                        for (a in e[b]) {
                            break;
                        }
                        if (!a) {
                            a = null;
                            delete e[b];
                        }
                    }
                }
                for (a in e) {
                    break;
                }
                if (!a) {
                    jQuery.removeData(c, this.dataKey);
                }
            }
        }
    }
});
jQuery(window).bind("unload", function () {
    jQuery.each(jQuery.timer.global, function (a, b) {
        jQuery.timer.remove(b);
    });
});
