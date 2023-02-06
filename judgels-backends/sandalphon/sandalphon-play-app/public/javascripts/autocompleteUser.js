require(["jquery", "jquery-ui"], function() {
    $(".user_autocomplete").autocomplete({
        source: function(request, response) {
            $.ajax({
                url: autocompleteUserAPIEndpoint,
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
