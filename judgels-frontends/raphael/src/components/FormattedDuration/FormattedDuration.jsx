import * as React from 'react';
import ReactFormattedDuration from 'react-intl-formatted-duration';

import '@formatjs/intl-pluralrules/polyfill-locales';

export function FormattedDuration({ value }) {
  const seconds = Math.floor(value / 1000);

  let format = '{hours} {minutes} {seconds}';
  if (seconds >= 86400) {
    format = `{days} ${format}`;
  }
  return <ReactFormattedDuration seconds={seconds} format={format} />;
}
