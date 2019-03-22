import * as React from 'react';
import { Item } from 'modules/api/sandalphon/problemBundle';
import { Card } from '@blueprintjs/core';
import { HtmlText } from 'components/HtmlText/HtmlText';
import { AnswerState } from '../../itemStatement';
import ItemEssayForm from './ItemEssayForm/ItemEssayForm';

export interface ItemEssayCardProps extends Item {
  className?: string;
  initialAnswer?: string;
  onSubmit?: () => Promise<any>;
}

export class ItemEssayCard extends React.PureComponent<ItemEssayCardProps> {
  render() {
    return (
      <Card className={this.props.className}>
        <HtmlText>{this.props.config.statement}</HtmlText>
        <ItemEssayForm
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
