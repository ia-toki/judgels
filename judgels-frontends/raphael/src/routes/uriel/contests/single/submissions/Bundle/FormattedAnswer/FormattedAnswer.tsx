import * as React from 'react';
import { FunctionComponent } from 'react';
import { Text } from '@blueprintjs/core';
import { ItemType } from 'modules/api/sandalphon/problemBundle';

export interface FormattedAnswerProps {
  answer?: string;
  type?: ItemType;
}

export const FormattedAnswer: FunctionComponent<FormattedAnswerProps> = ({ answer, type }) => {
  if (!answer) {
    return <>-</>;
  }
  if (type === ItemType.ShortAnswer) {
    return <Text ellipsize>{answer}</Text>;
  }
  if (type === ItemType.Essay) {
    return <pre>{answer}</pre>;
  }
  return <>{answer}</>;
};
