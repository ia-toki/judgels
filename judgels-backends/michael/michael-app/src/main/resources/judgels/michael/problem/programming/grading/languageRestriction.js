function checkState() {
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
}

document.addEventListener('DOMContentLoaded', function() {
  checkState();

  $('#isAllowedAll').on('click', function () {
    checkState();
  });
}, false);
