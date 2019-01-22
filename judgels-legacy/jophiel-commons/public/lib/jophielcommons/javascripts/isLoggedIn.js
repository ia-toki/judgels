require(["jquery"], function( __tes__ ) {
    $.ajax({
        url: jophielIsLoggedInUrl,
        type: 'GET',
        dataType: "jsonp",
        success: function( data ) {
            if (data) {
                window.location.replace(loginAuthUrl);
            }
        }
    });
});