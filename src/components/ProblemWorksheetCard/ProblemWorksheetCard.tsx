import * as React from 'react';

import { ProblemStatementCard } from './ProblemStatementCard';
import { ProblemSubmissionCard } from './ProblemSubmissionCard';
import { ProblemSubmissionFormData } from './ProblemSubmissionForm';
import { ProblemWorksheet } from '../../modules/api/sandalphon/problem';

import './ProblemWorksheetCard.css';

export interface ProblemWorksheetCardProps {
  alias: string;
  worksheet: ProblemWorksheet;
  onSubmit: (data: ProblemSubmissionFormData) => Promise<void>;
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
      <ProblemSubmissionCard
        config={this.props.worksheet.submissionConfig}
        onSubmit={this.props.onSubmit}
        reasonNotAllowedToSubmit={this.props.worksheet.reasonNotAllowedToSubmit}
      />
    );
  };
}
