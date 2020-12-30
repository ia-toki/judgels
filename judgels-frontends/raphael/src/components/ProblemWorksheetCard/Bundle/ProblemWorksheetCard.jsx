import { ProblemStatementCard } from './ProblemStatementCard/ProblemStatementCard';

import './ProblemWorksheetCard.css';

export function ProblemWorksheetCard({
  alias,
  worksheet: { statement, items, reasonNotAllowedToSubmit },
  latestSubmissions,
  onAnswerItem,
}) {
  return (
    <div className="bundle-problem-worksheet">
      <ProblemStatementCard
        alias={alias}
        statement={statement}
        onAnswerItem={onAnswerItem}
        items={items}
        latestSubmissions={latestSubmissions}
        reasonNotAllowedToSubmit={reasonNotAllowedToSubmit}
      />
    </div>
  );
}
