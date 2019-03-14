import * as React from 'react';
import { Card } from '@blueprintjs/core';
import { Item } from 'modules/api/sandalphon/problemBundle';
import { HtmlText } from 'components/HtmlText/HtmlText';
import { AnswerState } from '../../itemStatement';
import ItemShortAnswerForm from './ItemShortAnswerForm/ItemShortAnswerForm';

export interface ItemShortAnswerCardProps extends Item {
  className?: string;
  initialAnswer?: string;
  onSubmit?: () => Promise<any>;
}

export class ItemShortAnswerCard extends React.PureComponent<ItemShortAnswerCardProps> {
  render() {
    return (
      <Card className={this.props.className}>
        <HtmlText>{this.props.config.statement}</HtmlText>
        <ItemShortAnswerForm
          initialAnswer={this.props.initialAnswer}
          onSubmit={this.props.onSubmit}
          meta={this.props.meta}
          {...this.props}
          answerState={this.props.initialAnswer ? AnswerState.AnswerSaved : AnswerState.NotAnswered}
        />
      </Card>
    );
  }
}
