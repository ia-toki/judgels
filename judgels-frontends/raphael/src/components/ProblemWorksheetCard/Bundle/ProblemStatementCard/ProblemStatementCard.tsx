import * as React from 'react';
import { Item, ItemType } from 'modules/api/sandalphon/problemBundle';
import { ItemMultipleChoiceCard } from './ItemMultipleChoiceCard/ItemMultipleChoiceCard';
import { ItemSubmission } from 'modules/api/sandalphon/submissionBundle';
import { ProblemStatement } from 'modules/api/sandalphon/problem';
import { Divider } from '@blueprintjs/core';
import { HtmlText } from 'components/HtmlText/HtmlText';
import { ItemStatementCard } from './ItemStatementCard/ItemStatementCard';

import './ProblemStatementCard.css';
import { ItemShortAnswerCard } from './ItemShortAnswerCard/ItemShortAnswerCard';
import { ItemEssayCard } from './ItemEssayCard/ItemEssayCard';

export interface ProblemStatementCardProps {
  items: Item[];
  alias: string;
  statement: ProblemStatement;
  onAnswerItem: (itemJid: string, answer: string) => Promise<any>;
  latestSubmission: { [id: string]: ItemSubmission };
}

export class ProblemStatementCard extends React.Component<ProblemStatementCardProps> {
  generateOnAnswer = (itemJid: string) => {
    return async (answer?: string) => {
      return await this.props.onAnswerItem(itemJid, answer || '');
    };
  };

  renderStatement = (item: Item) => {
    return <ItemStatementCard className="bundle-problem-statement-item" key={item.meta} {...item} />;
  };

  renderShortAnswer = (item: Item) => {
    const latestSubmission = this.props.latestSubmission;
    const latestAnswer = latestSubmission[item.jid];
    const initialAnswer = latestAnswer && latestAnswer.answer;
    return (
      <ItemShortAnswerCard
        onSubmit={this.generateOnAnswer(item.jid)}
        className="bundle-problem-statement-item"
        key={item.meta}
        {...item}
        initialAnswer={initialAnswer}
      />
    );
  };

  renderEssay = (item: Item) => {
    const latestSubmission = this.props.latestSubmission;
    const latestAnswer = latestSubmission[item.jid];
    const initialAnswer = latestAnswer && latestAnswer.answer;
    return (
      <ItemEssayCard
        onSubmit={this.generateOnAnswer(item.jid)}
        className="bundle-problem-statement-item"
        key={item.meta}
        {...item}
        initialAnswer={initialAnswer}
      />
    );
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
          switch (item.type) {
            case ItemType.MultipleChoice:
              return this.renderMultipleChoice(item);
            case ItemType.ShortAnswer:
              return this.renderShortAnswer(item);
            case ItemType.Essay:
              return this.renderEssay(item);
            default:
              return this.renderStatement(item);
          }
        })}
      </>
    );
  }
}
