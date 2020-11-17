export function reallyConfirm(message: string) {
  return window.confirm(message) && window.confirm('Are you really sure?') && window.confirm('Final confirmation?');
}
