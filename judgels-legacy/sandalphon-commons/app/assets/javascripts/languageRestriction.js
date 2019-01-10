require(["jquery"], function( __jquery__ ) {
    function checkState() {
        if ($('#isAllowedAll').prop('checked')) {
            $('.allowedLanguageName').each(function () {
                $(this).prop('disabled', true);
                $(this).prop('checked', true);
            });
        } else {
            $('.allowedLanguageName').each(function () {
                $(this).prop('disabled', false);
            });
        }
    }

    $(document).ready(function () {
        checkState();

        $('#isAllowedAll').on('click', function () {
            checkState();
        });
    });
});
