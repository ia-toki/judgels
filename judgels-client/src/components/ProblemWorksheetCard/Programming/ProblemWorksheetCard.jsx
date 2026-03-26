import { Flex } from '@blueprintjs/labs';

import { ProblemStatementCard } from './ProblemStatementCard/ProblemStatementCard';
import { ProblemSubmissionCard } from './ProblemSubmissionCard/ProblemSubmissionCard';

import './ProblemWorksheetCard.scss';

export function ProblemWorksheetCard({
  alias,
  worksheet: { statement, limits, submissionConfig, reasonNotAllowedToSubmit },
  showTitle,
  showLimits,
  submissionWarning,
  gradingLanguage,
  onSubmit,
}) {
  const renderStatement = () => {
    return (
      <ProblemStatementCard
        alias={alias}
        statement={statement}
        limits={limits}
        showTitle={showTitle}
        showLimits={showLimits}
      />
    );
  };

  const renderSubmission = () => {
    if (!onSubmit) {
      return null;
    }
    return (
      <ProblemSubmissionCard
        config={submissionConfig}
        onSubmit={onSubmit}
        reasonNotAllowedToSubmit={reasonNotAllowedToSubmit}
        submissionWarning={submissionWarning}
        preferredGradingLanguage={gradingLanguage}
      />
    );
  };

  return (
    <Flex className="programming-problem-worksheet" flexDirection="column" gap={2}>
      {renderStatement()}
      {renderSubmission()}
    </Flex>
  );
}
