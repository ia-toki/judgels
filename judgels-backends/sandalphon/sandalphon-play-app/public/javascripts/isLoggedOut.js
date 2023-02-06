require(["jquery"], function( __tes__ ) {
    $.ajax({
        url: jophielIsLoggedInUrl,
        type: 'GET',
        dataType: "jsonp",
        success: function( data ) {
            if (!data) {
                window.location.replace(logoutUrl);
            }
        }
    });
    setInterval(function() {
        $.ajax({
            url: jophielIsLoggedInUrl,
            type: 'GET',
            dataType: "jsonp",
            success: function( data ) {
                if (!data) {
                    window.location.replace(logoutUrl);
                }
            }
        });
    }, 30000);
});