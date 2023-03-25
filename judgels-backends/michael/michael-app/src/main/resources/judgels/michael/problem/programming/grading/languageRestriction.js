document.addEventListener('DOMContentLoaded', () => {
  if (document.getElementById('isAllowedAll').checked) {
    document.querySelectorAll('.allowedLanguage').forEach(lang => {
      lang.disabled = true;
      lang.checked = true;
    });
  }

  document.getElementById('isAllowedAll').addEventListener('click', () => {
    if (document.getElementById('isAllowedAll').checked) {
      document.querySelectorAll('.allowedLanguage').forEach(lang => {
        lang.disabled = true;
        lang.checked = true;
      });
    } else {
      document.querySelectorAll('.allowedLanguage').forEach(lang => {
        lang.disabled = false;
        lang.checked = false;
      });
    }
  });
}, false);
