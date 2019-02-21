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
}

export class ProblemStatementCard extends React.PureComponent<ProblemStatementCardProps> {
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
              <ProblemItemMultipleChoiceCard className="bundle-problem-statement-item" key={item.meta} {...item} />
            );
          }
        })}
      </React.Fragment>
    );
  }
}
