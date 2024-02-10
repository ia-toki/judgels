import { Button, Callout, Intent } from '@blueprintjs/core';
import { BanCircle } from '@blueprintjs/icons';

import { ContentCard } from '../../../ContentCard/ContentCard';

export function ProblemSubmissionCard({ reasonNotAllowedToSubmit, resultsUrl }) {
  const redirectToResultsUrl = () => {
    window.location.href = resultsUrl;
  };

  const renderSubmissionForm = () => {
    if (reasonNotAllowedToSubmit) {
      return (
        <Callout icon={<BanCircle />} className="secondary-info">
          <span data-key="reason-not-allowed-to-submit">{reasonNotAllowedToSubmit}</span>
        </Callout>
      );
    }
    return (
      <Button intent={Intent.PRIMARY} onClick={redirectToResultsUrl}>
        Submit
      </Button>
    );
  };

  return <ContentCard>{renderSubmissionForm()}</ContentCard>;
}
