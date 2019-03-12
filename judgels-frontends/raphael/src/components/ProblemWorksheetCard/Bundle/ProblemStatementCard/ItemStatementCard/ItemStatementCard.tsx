import * as React from 'react';
import { Card } from '@blueprintjs/core';
import { Item } from 'modules/api/sandalphon/problemBundle';
import { HtmlText } from 'components/HtmlText/HtmlText';
import ItemStatementForm from '../../ItemStatementForm/ItemStatementForm';
import { AnswerState } from '../../itemStatement';

export interface ItemStatementCardProps extends Item {
  className?: string;
  initialAnswer?: string;
  onSubmit?: () => Promise<any>;
}

export class ItemStatementCard extends React.PureComponent<ItemStatementCardProps> {
  generateAnswerState() {
    return this.props.initialAnswer ? AnswerState.AnswerSaved : AnswerState.NotAnswered;
  }

  render() {
    return (
      <Card className={this.props.className}>
        <HtmlText>{this.props.config.statement}</HtmlText>
        <ItemStatementForm
          initialAnswer={this.props.initialAnswer}
          initialValues={{ value: this.props.initialAnswer }}
          onSubmit={this.props.onSubmit}
          meta={this.props.meta}
          answerState={this.generateAnswerState()}
        />
      </Card>
    );
  }
}
