import { Flex } from '@blueprintjs/labs';

import { ProblemStatementCard } from './ProblemStatementCard/ProblemStatementCard';
import { ProblemSubmissionCard } from './ProblemSubmissionCard/ProblemSubmissionCard';

import './ProblemWorksheetCard.scss';

export function ProblemWorksheetCard({
  alias,
  worksheet,
  showTitle,
  latestSubmissions,
  onAnswerItem,
  resultsUrl,
  disabled,
}) {
  const { statement, items, reasonNotAllowedToSubmit } = worksheet;
  return (
    <Flex className="bundle-problem-worksheet" flexDirection="column" gap={2}>
      <ProblemStatementCard
        alias={alias}
        statement={statement}
        showTitle={showTitle}
        onAnswerItem={onAnswerItem}
        items={items}
        latestSubmissions={latestSubmissions}
        disabled={disabled || !!reasonNotAllowedToSubmit}
      />
      <ProblemSubmissionCard reasonNotAllowedToSubmit={reasonNotAllowedToSubmit} resultsUrl={resultsUrl} />
    </Flex>
  );
}
