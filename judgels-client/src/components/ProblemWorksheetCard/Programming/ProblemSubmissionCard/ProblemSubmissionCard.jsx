import { Callout } from '@blueprintjs/core';
import { BanCircle } from '@blueprintjs/icons';

import { getAllowedGradingLanguages } from '../../../../modules/api/gabriel/language.js';
import { ContentCard } from '../../../ContentCard/ContentCard';
import ProblemSubmissionForm from '../ProblemSubmissionForm/ProblemSubmissionForm';

export function ProblemSubmissionCard({
  config: { sourceKeys, gradingEngine, gradingLanguageRestriction },
  reasonNotAllowedToSubmit,
  submissionWarning,
  preferredGradingLanguage,
  onSubmit,
}) {
  const renderSubmissionForm = () => {
    if (reasonNotAllowedToSubmit) {
      return (
        <Callout icon={<BanCircle />} className="secondary-info">
          <span data-key="reason-not-allowed-to-submit">{reasonNotAllowedToSubmit}</span>
        </Callout>
      );
    }

    const gradingLanguages = getAllowedGradingLanguages(gradingEngine, gradingLanguageRestriction);

    let defaultGradingLanguage = preferredGradingLanguage;
    if (gradingLanguages.indexOf(defaultGradingLanguage) === -1) {
      defaultGradingLanguage = gradingLanguages.length === 0 ? undefined : gradingLanguages[0];
    }

    const formProps = {
      sourceKeys,
      gradingEngine,
      gradingLanguages,
      submissionWarning,
      initialValues: {
        gradingLanguage: defaultGradingLanguage,
      },
    };

    return <ProblemSubmissionForm onSubmit={onSubmit} {...formProps} />;
  };

  return (
    <ContentCard>
      <h4>Submit solution</h4>
      {renderSubmissionForm()}
    </ContentCard>
  );
}
