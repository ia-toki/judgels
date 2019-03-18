import * as React from 'react';
import { Text } from '@blueprintjs/core';
import { ItemType } from 'modules/api/sandalphon/problemBundle';

export interface FormattedAnswerProps {
  answer?: string;
  type?: ItemType;
}

export class FormattedAnswer extends React.PureComponent<FormattedAnswerProps> {
  render() {
    const { answer, type } = this.props;
    if (!answer) {
      return '-';
    }
    if (type === ItemType.ShortAnswer) {
      return <Text ellipsize>{answer}</Text>;
    }
    if (type === ItemType.Essay) {
      return <pre>{answer}</pre>;
    }
    return answer;
  }
}
