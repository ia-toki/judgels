import * as React from 'react';
import { Item, ItemType } from 'modules/api/sandalphon/problemBundle';
import { ItemStatementCard } from './ItemStatementCard/ItemStatementCard';
import { ItemMultipleChoiceCard } from './ItemMultipleChoiceCard/ItemMultipleChoiceCard';
import { ItemSubmission } from 'modules/api/sandalphon/submissionBundle';
import { ProblemStatement } from 'modules/api/sandalphon/problem';
import { Divider } from '@blueprintjs/core';
import { HtmlText } from 'components/HtmlText/HtmlText';

import './ProblemStatementCard.css';

export interface ProblemStatementCardProps {
  items: Item[];
  alias: string;
  statement: ProblemStatement;
  onAnswerItem: (itemJid: string, answer: string) => any;
  latestSubmission: { [id: string]: ItemSubmission };
}

export class ProblemStatementCard extends React.Component<ProblemStatementCardProps> {
  generateOnAnswer = (itemJid: string) => {
    return (choice?: string) => {
      this.props.onAnswerItem(itemJid, choice || '');
    };
  };

  renderStatement = (item: Item) => {
    return <ItemStatementCard className="bundle-problem-statement-item" key={item.meta} {...item} />;
  };

  renderMultipleChoice = (item: Item) => {
    const latestSubmission = this.props.latestSubmission;
    const latestSub = latestSubmission[item.jid];
    const initialAnswer = latestSub && latestSub.answer;
    return (
      <ItemMultipleChoiceCard
        onChoiceChange={this.generateOnAnswer(item.jid)}
        className="bundle-problem-statement-item"
        key={item.meta}
        {...item}
        itemNumber={item.number!}
        initialAnswer={initialAnswer}
      />
    );
  };

  render() {
    const { alias, items, statement } = this.props;
    return (
      <>
        <h2 className="bundle-problem-statement__name">
          {alias}. {statement.title}
        </h2>
        <div className="bundle-problem-statement__text">
          <HtmlText>{statement.text}</HtmlText>
        </div>
        <Divider />
        {items.map(item => {
          if (item.type === ItemType.Statement) {
            return this.renderStatement(item);
          } else {
            return this.renderMultipleChoice(item);
          }
        })}
      </>
    );
  }
}
