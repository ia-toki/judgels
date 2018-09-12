import * as React from 'react';
import ReactFormattedDuration from 'react-intl-formatted-duration';

export interface FormattedDateProps {
  value: number;
}

const Text = () => null;

export const FormattedDuration = (props: FormattedDateProps) => {
  const seconds = Math.floor(props.value / 1000);

  let format = '{hours} {minutes} {seconds}';
  if (seconds >= 86400) {
    format = `{days} ${format}`;
  }

  return (
    <ReactFormattedDuration seconds={seconds} format={format} valueComponent={React.Fragment} textComponent={Text} />
  );
};
