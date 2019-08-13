import * as React from 'react';

import { ProblemStatementCard } from './ProblemStatementCard/ProblemStatementCard';
import { ProblemSubmissionFormData } from './ProblemSubmissionForm/ProblemSubmissionForm';
import { ProblemWorksheet } from 'modules/api/sandalphon/problemProgramming';
import { ProblemSubmissionCard } from './ProblemSubmissionCard/ProblemSubmissionCard';

import './ProblemWorksheetCard.css';

export interface ProblemWorksheetCardProps {
  alias: string;
  worksheet: ProblemWorksheet;
  onSubmit: (data: ProblemSubmissionFormData) => Promise<void>;
  submissionWarning?: string;
  gradingLanguage: string;
}

export class ProblemWorksheetCard extends React.PureComponent<ProblemWorksheetCardProps> {
  render() {
    return (
      <div className="programming-problem-worksheet">
        {this.renderStatement()}
        {this.renderSubmission()}
      </div>
    );
  }

  private renderStatement = () => {
    const { alias, worksheet } = this.props;
    return <ProblemStatementCard alias={alias} statement={worksheet.statement} limits={worksheet.limits} />;
  };

  private renderSubmission = () => {
    return (
      <ProblemSubmissionCard
        config={this.props.worksheet.submissionConfig}
        onSubmit={this.props.onSubmit}
        reasonNotAllowedToSubmit={this.props.worksheet.reasonNotAllowedToSubmit}
        submissionWarning={this.props.submissionWarning}
        preferredGradingLanguage={this.props.gradingLanguage}
      />
    );
  };
}
