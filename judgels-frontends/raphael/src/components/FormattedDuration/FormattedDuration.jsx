import * as React from 'react';
import ReactFormattedDuration from 'react-intl-formatted-duration';

import '@formatjs/intl-pluralrules/polyfill-locales';

export interface FormattedDateProps {
  value: number;
}

export const FormattedDuration = (props: FormattedDateProps) => {
  const seconds = Math.floor(props.value / 1000);

  let format = '{hours} {minutes} {seconds}';
  if (seconds >= 86400) {
    format = `{days} ${format}`;
  }
  return <ReactFormattedDuration seconds={seconds} format={format} />;
};
