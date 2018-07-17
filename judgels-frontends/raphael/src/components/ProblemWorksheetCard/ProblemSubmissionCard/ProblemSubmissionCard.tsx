import { Callout } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCard } from '../../ContentCard/ContentCard';
import ProblemSubmissionForm, { ProblemSubmissionFormData } from '../ProblemSubmissionForm/ProblemSubmissionForm';
import { ProblemSubmissionConfig } from '../../../modules/api/sandalphon/problem';

export interface ProblemSubmissionCardProps {
  config: ProblemSubmissionConfig;
  onSubmit: (data: ProblemSubmissionFormData) => Promise<void>;
  reasonNotAllowedToSubmit?: string;
  submissionWarning?: string;
}

export class ProblemSubmissionCard extends React.PureComponent<ProblemSubmissionCardProps> {
  render() {
    return (
      <ContentCard>
        <h4>Submit solution</h4>
        {this.renderSubmissionForm()}
      </ContentCard>
    );
  }

  private renderSubmissionForm = () => {
    if (this.props.reasonNotAllowedToSubmit) {
      return (
        <Callout icon="ban-circle" className="secondary-info">
          <span data-key="reason-not-allowed-to-submit">{this.props.reasonNotAllowedToSubmit}</span>
        </Callout>
      );
    }
    return <ProblemSubmissionForm {...this.props} />;
  };
}
