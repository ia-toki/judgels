import { Button, Callout, Intent } from '@blueprintjs/core';
import { BanCircle } from '@blueprintjs/icons';

import { ActionButtons } from '../../../ActionButtons/ActionButtons';
import { ContentCard } from '../../../ContentCard/ContentCard';

export function ProblemSubmissionCard({ reasonNotAllowedToSubmit, resultsUrl, onSubmit, isSubmitting }) {
  const submit = () => {
    const unconfirmedShortAnswers = document.querySelectorAll('input[type="text"]:not([readonly])');
    const unconfirmedEssays = document.querySelectorAll('textarea:not([readonly])');
    if (unconfirmedShortAnswers.length > 0 || unconfirmedEssays.length > 0) {
      window.alert('There are still unconfirmed answers! Make sure to click "Confirm answer" button.');
      return;
    }

    if (onSubmit) {
      onSubmit();
    } else {
      window.location.href = resultsUrl;
    }
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
      <ActionButtons>
        <Button intent={Intent.PRIMARY} loading={isSubmitting} onClick={submit}>
          Submit
        </Button>
      </ActionButtons>
    );
  };

  if (!resultsUrl && !onSubmit) {
    return null;
  }

  return <ContentCard>{renderSubmissionForm()}</ContentCard>;
}
