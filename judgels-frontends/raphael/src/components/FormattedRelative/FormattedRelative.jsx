import { FormattedRelativeTime } from 'react-intl';

import { selectUnit } from '@formatjs/intl-utils';
import '@formatjs/intl-locale/polyfill';
import '@formatjs/intl-pluralrules/polyfill';
import '@formatjs/intl-relativetimeformat/polyfill';
// prettier-ignore
import '@formatjs/intl-relativetimeformat/locale-data/en'

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
