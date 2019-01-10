require(["jquery", "bootstrap"], function() {
    $("#languages").change(function() {
        window.location = $("#languages").val();
    });
});
