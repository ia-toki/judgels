require(["jquery"], function( __tes__ ) {
    $.ajax({
        url: sandalphonTOTPURL,
        type: 'POST',
        data: body,
        contentType: 'application/x-www-form-urlencoded',
        success: function (data) {
            $(".problem_statement").html(data);
        }
    });
});