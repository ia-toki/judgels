export function FormattedDuration({ value }) {
  const oneSecond = 1000;
  const oneMinute = 60 * oneSecond;
  const oneHour = 60 * oneMinute;
  const oneDay = 24 * oneHour;

  let res = [];
  let val = value;

  const days = Math.floor(val / oneDay);
  if (days > 0) {
    res = [...res, days + ' day' + (days > 1 ? 's' : '')];
  }
  val = val % oneDay;

  const hours = Math.floor(val / oneHour);
  if (hours > 0) {
    res = [...res, hours + ' hour' + (hours > 1 ? 's' : '')];
  }
  val = val % oneHour;

  const minutes = Math.floor(val / oneMinute);
  if (minutes > 0) {
    res = [...res, minutes + ' minute' + (minutes > 1 ? 's' : '')];
  }
  val = val % oneMinute;

  const seconds = Math.floor(val / oneSecond);
  if (seconds > 0) {
    res = [...res, seconds + ' second' + (seconds > 1 ? 's' : '')];
  }

  return res.join(' ');
}
