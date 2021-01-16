require(["jquery", "moment", "bootstrap", "bootstrap-datetimepicker"], function() {
    $(".datepicker").datetimepicker({
        locale: language,
        format: "DD-MM-YYYY"
    });
});
