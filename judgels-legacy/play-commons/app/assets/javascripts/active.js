require(["jquery", "bootstrap"], function() {
    $($(".breadcrumb-link").get().reverse()).each(function() {
        var href = $(this).attr("href");
        $("a[href='" + href +"']").each(function() {
            var parent = $(this).parent();
            if (!$(this).hasClass("breadcrumb-link") && parent.siblings(".active").size() === 0) {
                parent.addClass("active");
            }
            if (parent.is("li")) {
                parent.parentsUntil("body", "li").addClass("active");
            }
        });
    });
    $('[data-toggle="tooltip"]').tooltip();
});
