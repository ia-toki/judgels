document.addEventListener('DOMContentLoaded', function() {
  if ($('#isAllowedAll').prop('checked')) {
    $('.allowedLanguage').each(function () {
      $(this).prop('disabled', true);
      $(this).prop('checked', true);
    });
  }

  $('#isAllowedAll').on('click', function () {
    if ($('#isAllowedAll').prop('checked')) {
      $('.allowedLanguage').each(function () {
        $(this).prop('disabled', true);
        $(this).prop('checked', true);
      });
    } else {
      $('.allowedLanguage').each(function () {
        $(this).prop('disabled', false);
        $(this).prop('checked', false);
      });
    }
  });
}, false);
