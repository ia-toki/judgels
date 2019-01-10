require(["jquery", "jquery-timer", "moment"], function() {
    var moment = require("moment");
    moment.locale(language);
    var serverDateTime = moment($("time#server-time").attr("datetime"));
    var clientDateTime = moment();
    var updateServerTime = function() {
        var currentDateTime = moment();
        serverDateTime.add(currentDateTime.valueOf() - clientDateTime.valueOf(), "ms");
        clientDateTime = currentDateTime;
        $("time#server-time").text(serverDateTime.format("DD-MMM-YYYY HH:mm:ss Z"));
    };
    $(document).ready(updateServerTime);
    $(document).stopTime("server-time");
    $(document).everyTime("1s", "server-time", updateServerTime);

    var updateDisplayTime = function() {
        var elements = $("time.display-time");
        for (var i = 0; i < elements.length; i++) {
            var element = $(elements[i]);
            var datetime = moment(element.attr("datetime"));
            element.attr("title", datetime.format("DD-MMM-YYYY HH:mm:ss Z"));
            element.text(datetime.from(serverDateTime));
        }
    };
    $(document).ready(updateDisplayTime);
    $(document).stopTime("display-time");
    $(document).everyTime("60s", "display-time", updateDisplayTime);
});
