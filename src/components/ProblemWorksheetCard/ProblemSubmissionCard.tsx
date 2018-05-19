import * as React from 'react';

import { ContentCard } from '../ContentCard/ContentCard';
import { ProblemSubmissionConfiguration } from '../../modules/api/sandalphon/problem';
import ProblemSubmissionForm, { ProblemSubmissionFormData } from './ProblemSubmissionForm';

export interface ProblemSubmissionCardProps {
  config: ProblemSubmissionConfiguration;
  onSubmit: (data: ProblemSubmissionFormData) => void;
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
    return <ProblemSubmissionForm {...this.props} />;
  };
}
