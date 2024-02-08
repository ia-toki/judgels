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
    <div className="bundle-problem-worksheet">
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
    </div>
  );
}
