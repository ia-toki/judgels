export function FormattedDate({ value, showSeconds }) {
  return new Date(value).toLocaleString('default', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: 'numeric',
    hour12: false,
    minute: 'numeric',
    second: showSeconds ? 'numeric' : undefined,
  });
}
