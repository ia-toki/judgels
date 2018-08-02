import * as React from 'react';

import { ProblemWorksheet } from 'modules/api/sandalphon/problem';

import { ProblemStatementCard } from './ProblemStatementCard/ProblemStatementCard';
import { ProblemSubmissionCard } from './ProblemSubmissionCard/ProblemSubmissionCard';
import { ProblemSubmissionFormData } from './ProblemSubmissionForm/ProblemSubmissionForm';

import './ProblemWorksheetCard.css';

export interface ProblemWorksheetCardProps {
  alias: string;
  worksheet: ProblemWorksheet;
  onSubmit: (data: ProblemSubmissionFormData) => Promise<void>;
  submissionWarning?: string;
}

export class ProblemWorksheetCard extends React.PureComponent<ProblemWorksheetCardProps> {
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
        submissionWarning={this.props.submissionWarning}
      />
    );
  };
}
