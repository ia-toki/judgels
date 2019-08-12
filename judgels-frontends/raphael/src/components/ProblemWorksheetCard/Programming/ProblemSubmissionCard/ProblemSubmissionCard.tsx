import { Callout } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCard } from 'components/ContentCard/ContentCard';
import { getAllowedGradingLanguages } from 'modules/api/gabriel/language';
import { ProblemSubmissionConfig } from 'modules/api/sandalphon/problemProgramming';
import ProblemSubmissionForm, { ProblemSubmissionFormData } from '../ProblemSubmissionForm/ProblemSubmissionForm';

export interface ProblemSubmissionCardProps {
  config: ProblemSubmissionConfig;
  onSubmit: (data: ProblemSubmissionFormData) => Promise<void>;
  reasonNotAllowedToSubmit?: string;
  submissionWarning?: string;
  preferredGradingLanguage: string;
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
    const { config, onSubmit, reasonNotAllowedToSubmit, submissionWarning } = this.props;

    if (reasonNotAllowedToSubmit) {
      return (
        <Callout icon="ban-circle" className="secondary-info">
          <span data-key="reason-not-allowed-to-submit">{reasonNotAllowedToSubmit}</span>
        </Callout>
      );
    }

    const gradingLanguages = getAllowedGradingLanguages(config.gradingEngine, config.gradingLanguageRestriction);

    let defaultGradingLanguage: string | undefined = this.props.preferredGradingLanguage;
    if (gradingLanguages.indexOf(defaultGradingLanguage) === -1) {
      defaultGradingLanguage = gradingLanguages.length === 0 ? undefined : gradingLanguages[0];
    }

    const formProps = {
      sourceKeys: config.sourceKeys,
      gradingEngine: config.gradingEngine,
      gradingLanguages,
      submissionWarning,
      initialValues: {
        gradingLanguage: defaultGradingLanguage,
      },
    };

    return <ProblemSubmissionForm onSubmit={onSubmit} {...formProps} />;
  };
}
