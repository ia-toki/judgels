import * as React from 'react';
import * as HTMLReactParser from 'html-react-parser';

import { ContentCard } from '../ContentCard/ContentCard';
import { ProblemStatement } from '../../modules/api/sandalphon/problem';

import './ProblemStatementDetails.css';

export interface ProblemStatementDetailsProps {
  alias: string;
  statement: ProblemStatement;
}

export class ProblemStatementDetails extends React.Component<ProblemStatementDetailsProps> {
  render() {
    return (
      <div className="statement">
        {this.renderStatement()}
        {this.renderSubmission()}
      </div>
    );
  }

  private renderStatement = () => {
    const { alias, statement } = this.props;
    return (
      <ContentCard>
        <h2 className="statement__name">
          {alias}. {statement.name}
        </h2>
        <table className="pt-html-table pt-small statement__limits">
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
        <div className="statement__text html-text">{HTMLReactParser(statement.text)}</div>
      </ContentCard>
    );
  };

  private renderSubmission = () => {
    return (
      <ContentCard>
        <h4>Submit solution</h4>
      </ContentCard>
    );
  };

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
