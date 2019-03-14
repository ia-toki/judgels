import * as React from 'react';
import { FunctionComponent } from 'react';
import { Text } from '@blueprintjs/core';

export interface FormattedAnswerProps {
  answer?: string;
}

export const FormattedAnswer: FunctionComponent<FormattedAnswerProps> = ({ answer }) => {
  return <Text ellipsize>{answer || '-'}</Text>;
};
