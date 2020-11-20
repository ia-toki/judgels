import * as React from 'react';
import { FormattedRelativeTime } from 'react-intl';

import { selectUnit } from '@formatjs/intl-utils';
import '@formatjs/intl-relativetimeformat/polyfill-locales';

export function FormattedRelative(props) {
  if (!props.value) {
    return null;
  }
  const { value, unit } = selectUnit(props.value, Date.now(), {
    second: 59,
    minute: 59,
    hour: 23,
    day: 30,
  });
  return <FormattedRelativeTime value={value} unit={unit} />;
}
