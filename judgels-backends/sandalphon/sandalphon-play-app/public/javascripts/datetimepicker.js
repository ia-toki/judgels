require(["jquery", "moment", "bootstrap", "bootstrap-datetimepicker"], function() {
    $(".datetimepicker").datetimepicker({
        locale: language,
        format: "DD-MM-YYYY HH:mm:ss Z"
    });
});
