import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { HtmlText } from 'components/HtmlText/HtmlText';
import { ContentCard } from 'components/ContentCard/ContentCard';
import { ProblemLimits, ProblemStatement } from 'modules/api/sandalphon/problem';

import './ProblemStatementCard.css';

export interface ProblemStatementCardProps {
  alias: string;
  statement: ProblemStatement;
  limits: ProblemLimits;
}

export class ProblemStatementCard extends React.PureComponent<ProblemStatementCardProps> {
  render() {
    const { alias, statement, limits } = this.props;
    return (
      <ContentCard>
        <h2 className="problem-statement__name">
          {alias}. {statement.title}
        </h2>
        <HTMLTable condensed className="problem-statement__limits">
          <tbody>
            <tr>
              <td>Time limit</td>
              <td>{this.renderTimeLimit(limits.timeLimit)}</td>
            </tr>
            <tr>
              <td>Memory limit</td>
              <td>{this.renderMemoryLimit(limits.memoryLimit)}</td>
            </tr>
          </tbody>
        </HTMLTable>
        <div className="problem-statement__text">
          <HtmlText>{statement.text}</HtmlText>
        </div>
      </ContentCard>
    );
  }

  private renderTimeLimit = (timeLimit: number) => {
    if (!timeLimit) {
      return '-';
    }
    if (timeLimit % 1000 === 0) {
      return timeLimit / 1000 + ' s';
    }
    return timeLimit + ' ms';
  };

  private renderMemoryLimit = (memoryLimit: number) => {
    if (!memoryLimit) {
      return '-';
    }
    if (memoryLimit % 1024 === 0) {
      return memoryLimit / 1024 + ' MB';
    }
    return memoryLimit + ' KB';
  };
}
