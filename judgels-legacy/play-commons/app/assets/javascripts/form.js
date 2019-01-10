// Reference: http://stackoverflow.com/questions/2830542/prevent-double-submission-of-forms-in-jquery

require(["jquery", "select2"], function() {
    function prepareRadioButton() {
        $("input[type='radio']").change(function(e) {
            var that = $(this);
            var all = $("input[type='radio'][name='" + that.attr("name") + "']");
            that.prop('onclick',null).off('click');
            that.click(function(e) {
                var that = $(this);
                var checked = $("input[type='radio'][name='" + that.attr("name") + "']:checked");
                if (checked.hasClass('singleCheck')) {
                    checked.attr("checked", false);
                    checked.removeClass("singleCheck");
                }
            });
            all.removeClass("singleCheck");
            setTimeout(function() {
                that.addClass("singleCheck");
            }, 100);
        });
    }
    function prepareForm() {
        $("form").submit(function (e) {
            var $form = $(this);

            if ($form.data('submitted') === true) {
                // Previously submitted - don't submit again
                e.preventDefault();
            } else {
                // Mark it so that the next submit can be ignored
                $form.data('submitted', true);
            }

            return this;
        });
    }
    $(document).ready(function() {
        prepareForm();
        prepareRadioButton();
        $(".selectpicker").select2();
    });
    $(document).ajaxComplete(function () {
        prepareRadioButton();
    });
});
