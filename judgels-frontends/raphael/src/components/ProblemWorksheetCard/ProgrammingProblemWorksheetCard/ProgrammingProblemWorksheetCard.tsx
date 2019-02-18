import * as React from 'react';

import { ProgrammingProblemWorksheet } from 'modules/api/sandalphon/problem';

import { ProgrammingProblemStatementCard } from './ProgrammingProblemStatementCard/ProgrammingProblemStatementCard';
import { ProgrammingProblemSubmissionCard } from './ProgrammingProblemSubmissionCard/ProgrammingProblemSubmissionCard';
import { ProgrammingProblemSubmissionFormData } from './ProgrammingProblemSubmissionForm/ProgrammingProblemSubmissionForm';

import './ProgrammingProblemWorksheetCard.css';

export interface ProgrammingProblemWorksheetCardProps {
  alias: string;
  worksheet: ProgrammingProblemWorksheet;
  onSubmit: (data: ProgrammingProblemSubmissionFormData) => Promise<void>;
  submissionWarning?: string;
}

export class ProgrammingProblemWorksheetCard extends React.PureComponent<ProgrammingProblemWorksheetCardProps> {
  render() {
    return (
      <div className="problem-worksheet">
        {this.renderStatement()}
        {this.renderSubmission()}
      </div>
    );
  }

  private renderStatement = () => {
    const { alias, worksheet } = this.props;
    return <ProgrammingProblemStatementCard alias={alias} statement={worksheet.statement} limits={worksheet.limits} />;
  };

  private renderSubmission = () => {
    return (
      <ProgrammingProblemSubmissionCard
        config={this.props.worksheet.submissionConfig}
        onSubmit={this.props.onSubmit}
        reasonNotAllowedToSubmit={this.props.worksheet.reasonNotAllowedToSubmit}
        submissionWarning={this.props.submissionWarning}
      />
    );
  };
}
