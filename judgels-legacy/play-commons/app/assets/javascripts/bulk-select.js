require(["jquery"], function() {
    $(document).ready(function() {
        $('#selectAll').on('click', function() {
            var checked = $('#selectAll').prop('checked');
            $('.checkboxJid').each(function() {
                $(this).prop('checked', checked);
            });
        });
    });
});
