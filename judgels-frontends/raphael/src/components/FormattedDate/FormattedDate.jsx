import * as React from 'react';
import { FormattedDate as ReactFormattedDate } from 'react-intl';

export interface FormattedDateProps {
  value: number;
  showSeconds?: boolean;
}

export const FormattedDate = (props: FormattedDateProps) => (
  <ReactFormattedDate
    value={props.value}
    year="numeric"
    month="short"
    day="numeric"
    hour="numeric"
    hour12={false}
    minute="numeric"
    second={props.showSeconds ? 'numeric' : undefined}
  />
);
