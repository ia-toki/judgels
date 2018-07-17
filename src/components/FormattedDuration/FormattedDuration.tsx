import * as React from 'react';
import ReactFormattedDuration from 'react-intl-formatted-duration';

export interface FormattedDateProps {
  value: number;
}
const Span = props => <span {...props} />;

export const FormattedDuration = (props: FormattedDateProps) => {
  return (
    <ReactFormattedDuration
      seconds={Math.floor(props.value / 1000)}
      format="{days} {hours} {minutes} {seconds}"
      textComponent={Span}
    />
  );
};
