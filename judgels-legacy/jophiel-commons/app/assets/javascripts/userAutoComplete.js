require(["jquery", "jquery-ui"], function( __tes__ ) {
    $(".user_autocomplete").autocomplete({
        source: function( request, response ) {
            $.ajax({
                url: jophielAutoCompleteUrl,
                type: 'GET',
                data: {
                    term: request.term
                },
                dataType: "jsonp",
                success: function( data ) {
                    response( data );
                }
            });
        },
        minLength: 2
    });
});