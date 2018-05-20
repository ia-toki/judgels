import { Callout } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCard } from '../ContentCard/ContentCard';
import ProblemSubmissionForm, { ProblemSubmissionFormData } from './ProblemSubmissionForm';
import { ProblemSubmissionConfiguration } from '../../modules/api/sandalphon/problem';

export interface ProblemSubmissionCardProps {
  config: ProblemSubmissionConfiguration;
  onSubmit: (data: ProblemSubmissionFormData) => Promise<void>;
  reasonNotAllowedToSubmit?: string;
  submissionWarning?: string;
}

export class ProblemSubmissionCard extends React.Component<ProblemSubmissionCardProps> {
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
      return <Callout icon="ban-circle">{this.props.reasonNotAllowedToSubmit}</Callout>;
    }
    return <ProblemSubmissionForm {...this.props} />;
  };
}
