require(["jquery", "jquery-ui"], function( __tes__ ) {
    $(".course_autocomplete").autocomplete({
        source: function( request, response ) {
            $.ajax({
                url: courseAutoCompleteUrl,
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