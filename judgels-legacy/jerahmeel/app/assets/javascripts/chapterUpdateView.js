require(["jquery"], function( __tes__ ) {
    $(document).ready(function() {
        $.ajax({
            url: chapterUpdateViewUrl,
            type: 'POST',
            data: {
                courseChapterId: courseChapterId
            },
            contentType: 'application/x-www-form-urlencoded',
        });
    });
});