export function FormattedRelative({ value }) {
  if (!value) {
    return null;
  }

  const oneSecond = 1000;
  const oneMinute = 60 * oneSecond;
  const oneHour = 60 * oneMinute;
  const oneDay = 24 * oneHour;
  const oneMonth = 30 * oneDay;
  const oneYear = 365 * oneDay;

  let diff = new Date().getTime() - value;

  if (diff >= 0 && diff < 5 * oneSecond) {
    return 'just now';
  }

  const sign = Math.sign(diff);
  diff = Math.abs(diff);

  let res = [];

  if (diff >= oneYear) {
    const years = Math.round(diff / oneYear);
    res = [...res, years + ' year' + (years > 1 ? 's' : '')];
  } else if (diff >= oneMonth) {
    const months = Math.round(diff / oneMonth);
    res = [...res, months + ' month' + (months > 1 ? 's' : '')];
  } else if (diff >= oneDay) {
    const days = Math.round(diff / oneDay);
    res = [...res, days + ' day' + (days > 1 ? 's' : '')];
  } else if (diff >= oneHour) {
    const hours = Math.floor(diff / oneHour);
    if (hours > 0) {
      res = [...res, hours + ' hour' + (hours > 1 ? 's' : '')];
      diff = diff % oneHour;
    }
  } else if (diff >= oneMinute) {
    const minutes = Math.floor(diff / oneMinute);
    if (minutes > 0) {
      res = [...res, minutes + ' min' + (minutes > 1 ? 's' : '')];
      diff = diff % oneMinute;
    }
  } else {
    const seconds = Math.floor(diff / oneSecond);
    if (seconds > 0) {
      res = [...res, seconds + ' sec' + (seconds > 1 ? 's' : '')];
    }
  }

  if (sign > 0) {
    return res.join(' ') + ' ago';
  } else {
    return 'in ' + res.join(' ');
  }
}
