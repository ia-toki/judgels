function clearChoices(el) {
  el.parentElement.querySelectorAll('input').forEach(input => { input.checked = false; });
}
