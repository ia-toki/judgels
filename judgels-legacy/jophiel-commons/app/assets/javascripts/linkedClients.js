require(["jquery", "jquery-ui"], function() {
    $.ajax({
        url: linkedClientsAPIEndpoint,
        type: 'GET',
        dataType: "jsonp",
        success: function( data ) {
            jQuery.each(data, function(key, val) {
                var panel ="<div class=\"clearfix\">";
                panel += "<a class=\"btn btn-primary col-md-12\" href=\"" + key + "\">" + val + "</a>";
                panel += "</div>";
                panel += "<br />";
                $(".linked_clients").append(panel);
            });
        }
    });
});