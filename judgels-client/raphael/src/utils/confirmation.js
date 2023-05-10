export function reallyConfirm(message) {
  return window.confirm(message) && window.confirm('Are you really sure?') && window.confirm('Final confirmation?');
}
