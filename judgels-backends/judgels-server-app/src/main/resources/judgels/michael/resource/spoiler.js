document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('.spoiler').forEach(spoiler => {
    spoiler.addEventListener('click', event => {
      var content = spoiler.getElementsByTagName('div')[0];
      content.style.display = (content.style.display == 'block') ? 'none': 'block';
    });
  });
}, false);
