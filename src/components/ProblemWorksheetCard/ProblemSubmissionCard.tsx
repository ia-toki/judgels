import { Callout, Intent } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCard } from '../ContentCard/ContentCard';
import { ProblemSubmissionConfiguration } from '../../modules/api/sandalphon/problem';
import ProblemSubmissionForm, { ProblemSubmissionFormData } from './ProblemSubmissionForm';

export interface ProblemSubmissionCardProps {
  config: ProblemSubmissionConfiguration;
  onSubmit: (data: ProblemSubmissionFormData) => Promise<void>;
  reasonNotAllowedToSubmit?: string;
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
      return (
        <Callout intent={Intent.DANGER} icon="ban-circle">
          {this.props.reasonNotAllowedToSubmit}
        </Callout>
      );
    }
    return <ProblemSubmissionForm {...this.props} />;
  };
}
