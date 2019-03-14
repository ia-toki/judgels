import * as React from 'react';
import { FunctionComponent } from 'react';

export interface FormattedAnswerProps {
  answer?: string;
}

export const FormattedAnswer: FunctionComponent<FormattedAnswerProps> = ({ answer }) => {
  return <>{answer || '-'}</>;
};
