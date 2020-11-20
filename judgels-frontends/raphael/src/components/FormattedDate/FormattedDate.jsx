import * as React from 'react';
import { FormattedDate as ReactFormattedDate } from 'react-intl';

export function FormattedDate({ value, showSeconds }) {
  return (
    <ReactFormattedDate
      value={value}
      year="numeric"
      month="short"
      day="numeric"
      hour="numeric"
      hour12={false}
      minute="numeric"
      second={showSeconds ? 'numeric' : undefined}
    />
  );
}
