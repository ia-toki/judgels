import { Callout, Intent } from '@blueprintjs/core';
import { BanCircle } from '@blueprintjs/icons';

import { ContentCard } from '../../../ContentCard/ContentCard';
import { ButtonLink } from '../../../ButtonLink/ButtonLink';

export function ProblemSubmissionCard({ reasonNotAllowedToSubmit, resultsUrl }) {
  const renderSubmissionForm = () => {
    if (reasonNotAllowedToSubmit) {
      return (
        <Callout icon={<BanCircle />} className="secondary-info">
          <span data-key="reason-not-allowed-to-submit">{reasonNotAllowedToSubmit}</span>
        </Callout>
      );
    }
    return (
      <ButtonLink intent={Intent.PRIMARY} to={resultsUrl}>
        Finish and show results
      </ButtonLink>
    );
  };

  return <ContentCard>{renderSubmissionForm()}</ContentCard>;
}
