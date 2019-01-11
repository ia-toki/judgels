require(["jquery"], function( __tes__ ) {
    $.ajax({
        url: sandalphonLessonTOTPURL,
        type: 'POST',
        data: body,
        contentType: 'application/x-www-form-urlencoded',
        success: function (data) {
            $(".lesson_statement").html(data);
        }
    });
});