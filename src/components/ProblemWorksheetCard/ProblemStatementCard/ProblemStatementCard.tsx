import * as React from 'react';
import * as HTMLReactParser from 'html-react-parser';

import { ContentCard } from '../../ContentCard/ContentCard';
import { ProblemStatement } from '../../../modules/api/sandalphon/problem';

import './ProblemStatementCard.css';

export interface ProblemStatementCardProps {
  alias: string;
  statement: ProblemStatement;
}

export class ProblemStatementCard extends React.PureComponent<ProblemStatementCardProps> {
  render() {
    const { alias, statement } = this.props;
    return (
      <ContentCard>
        <h2 className="problem-statement__name">
          {alias}. {statement.name}
        </h2>
        <table className="pt-html-table pt-small problem-statement__limits">
          <tbody>
            <tr>
              <td>Time limit</td>
              <td>{this.renderTimeLimit(statement.timeLimit)}</td>
            </tr>
            <tr>
              <td>Memory limit</td>
              <td>{this.renderMemoryLimit(statement.memoryLimit)}</td>
            </tr>
          </tbody>
        </table>
        <div className="problem-statement__text html-text">{HTMLReactParser(statement.text)}</div>
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
