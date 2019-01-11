require(["jquery"], function( __tes__ ) {
    $(document).ready(function() {
        $.ajax({
            url: lessonUpdateViewUrl,
            type: 'POST',
            data: {
                chapterLessonId: chapterLessonId
            },
            contentType: 'application/x-www-form-urlencoded',
        });
    });
});