import { Divider } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { Item, ItemType } from '../../../../modules/api/sandalphon/problemBundle';
import { ItemSubmission } from '../../../../modules/api/sandalphon/submissionBundle';
import { ProblemStatement } from '../../../../modules/api/sandalphon/problem';

import { ItemStatementCard } from './ItemStatementCard/ItemStatementCard';
import { ItemMultipleChoiceCard } from './ItemMultipleChoiceCard/ItemMultipleChoiceCard';
import { ItemShortAnswerCard } from './ItemShortAnswerCard/ItemShortAnswerCard';
import { ItemEssayCard } from './ItemEssayCard/ItemEssayCard';

import './ProblemStatementCard.css';

export interface ProblemStatementCardProps {
  items: Item[];
  alias?: string;
  statement: ProblemStatement;
  onAnswerItem: (itemJid: string, answer: string) => Promise<any>;
  latestSubmissions: { [id: string]: ItemSubmission };
  reasonNotAllowedToSubmit?: string;
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
    const latestSubmissions = this.props.latestSubmissions;
    const latestAnswer = latestSubmissions[item.jid];
    const initialAnswer = latestAnswer && latestAnswer.answer;
    return (
      <ItemShortAnswerCard
        onSubmit={this.generateOnAnswer(item.jid)}
        className="bundle-problem-statement-item"
        key={item.meta}
        {...item}
        itemNumber={item.number!}
        initialAnswer={initialAnswer}
        disabled={!!this.props.reasonNotAllowedToSubmit}
      />
    );
  };

  renderEssay = (item: Item) => {
    const latestSubmissions = this.props.latestSubmissions;
    const latestAnswer = latestSubmissions[item.jid];
    const initialAnswer = latestAnswer && latestAnswer.answer;
    return (
      <ItemEssayCard
        onSubmit={this.generateOnAnswer(item.jid)}
        className="bundle-problem-statement-item"
        key={item.meta}
        {...item}
        itemNumber={item.number!}
        initialAnswer={initialAnswer}
        disabled={!!this.props.reasonNotAllowedToSubmit}
      />
    );
  };

  renderMultipleChoice = (item: Item) => {
    const latestSubmissions = this.props.latestSubmissions;
    const latestSub = latestSubmissions[item.jid];
    const initialAnswer = latestSub && latestSub.answer;
    return (
      <ItemMultipleChoiceCard
        onChoiceChange={this.generateOnAnswer(item.jid)}
        className="bundle-problem-statement-item"
        key={item.meta}
        {...item}
        itemNumber={item.number!}
        initialAnswer={initialAnswer}
        disabled={!!this.props.reasonNotAllowedToSubmit}
      />
    );
  };

  render() {
    const { alias, items, statement } = this.props;
    return (
      <ContentCard>
        <h2 className="bundle-problem-statement__name">
          {alias ? `${alias}. ` : ''}
          {statement.title}
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
      </ContentCard>
    );
  }
}
