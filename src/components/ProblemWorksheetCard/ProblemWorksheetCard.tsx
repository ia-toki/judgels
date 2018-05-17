import * as React from 'react';

import { ContentCard } from '../ContentCard/ContentCard';
import { ProblemStatementCard } from './ProblemStatementCard';
import { ProblemWorksheet } from '../../modules/api/sandalphon/problem';

import './ProblemWorksheetCard.css';

export interface ProblemWorksheetCardProps {
  alias: string;
  worksheet: ProblemWorksheet;
}

export class ProblemWorksheetCard extends React.Component<ProblemWorksheetCardProps> {
  render() {
    return (
      <div className="problem-worksheet">
        {this.renderStatement()}
        {this.renderSubmission()}
      </div>
    );
  }

  private renderStatement = () => {
    return <ProblemStatementCard alias={this.props.alias} statement={this.props.worksheet.statement} />;
  };

  private renderSubmission = () => {
    return (
      <ContentCard>
        <h4>Submit solution</h4>
      </ContentCard>
    );
  };
}
