import * as React from 'react';
import { Divider } from '@blueprintjs/core';
import { Item, ItemType } from 'modules/api/sandalphon/problemBundle';
import { ProblemItemStatementCard } from './ProblemItemStatementCard/ProblemItemStatementCard';
import { ProblemItemMultipleChoiceCard } from './ProblemItemMultipleChoiceCard/ProblemItemMultipleChoiceCard';

import './ProblemStatementCard.css';

export interface ProblemStatementCardProps {
  title: string;
  alias: string;
  items: Item[];
  onItemAnswered: (itemJid: string, answer?: string) => any;
}

export class ProblemStatementCard extends React.Component<ProblemStatementCardProps> {
  generateOnAnswer = (itemJid: string) => {
    return (choice?: string) => {
      this.props.onItemAnswered(itemJid, choice);
    };
  };
  render() {
    const { title, alias, items } = this.props;
    return (
      <React.Fragment>
        <h2 className="bundle-problem-statement__name">
          {alias}. {title}
        </h2>
        <Divider />
        {items.map(item => {
          if (item.type === ItemType.Statement) {
            return <ProblemItemStatementCard className="bundle-problem-statement-item" key={item.meta} {...item} />;
          } else {
            return (
              <ProblemItemMultipleChoiceCard
                onChoiceChange={this.generateOnAnswer(item.jid)}
                className="bundle-problem-statement-item"
                key={item.meta}
                {...item}
              />
            );
          }
        })}
      </React.Fragment>
    );
  }
}
