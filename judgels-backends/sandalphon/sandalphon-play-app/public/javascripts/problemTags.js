require(["jquery"], function( __jquery__ ) {
    function checkStates() {
        $('.problemTag').each(function() {
            var tag = $(this);
            var tagValue = tag.prop('value');
            if (!tagValue.includes(': ') && tag.prop('checked')) {
                var indeterminate = false;
                $('.problemTag').each(function() {
                    var child = $(this);
                    var childValue = child.prop('value');
                    if (tagValue !== childValue && childValue.startsWith(tagValue) && child.prop('checked')) {
                        indeterminate = true;
                    }
                });
                tag.prop('indeterminate', indeterminate);
            }
        });
    }

    function checkState(tag) {
        var tagValue = tag.prop('value');
        if (tag.prop('checked')) {
            $('.problemTag').each(function() {
                var parent = $(this);
                var parentValue = parent.prop('value');
                if (tagValue !== parentValue && tagValue.startsWith(parentValue)) {
                    parent.prop('checked', true);
                }
            });
            $('.problemTag').each(function() {
                var child = $(this);
                var childValue = child.prop('value');
                if (tagValue !== childValue && childValue.startsWith(tagValue)) {
                    child.prop('checked', false);
                    child.prop('disabled', true);
                }
            });
        } else {
            $('.problemTag').each(function() {
                var child = $(this);
                var childValue = child.prop('value');
                if (tagValue !== childValue && childValue.startsWith(tagValue)) {
                    child.prop('checked', false);
                    child.prop('disabled', false);
                }
            });
        }
    }

    $(document).ready(function () {
        checkStates();

        $('.problemTag').each(function() {
            $(this).on('click', function(e) {
                checkState($(e.target));
                checkStates();
            });
        });
    });
});
