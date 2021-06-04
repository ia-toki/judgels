import { Intent, Tag } from '@blueprintjs/core';

import { ContentCardLink } from '../../../../../../components/ContentCardLink/ContentCardLink';
import { ContestProblemStatus } from '../../../../../../modules/api/uriel/contestProblem';

import './ContestProblemCard.scss';

export function ContestProblemCard({
  contest,
  problem: { alias, points, status, submissionsLimit },
  problemName,
  totalSubmissions,
}) {
  const problemPoints = points === null || points === undefined ? '' : ` [${points} points]`;

  const renderStatus = () => {
    if (status === ContestProblemStatus.Closed) {
      return <Tag intent={Intent.DANGER}>CLOSED</Tag>;
    }
    if (!!submissionsLimit) {
      return <span>{submissionsLimit - totalSubmissions} submissions left</span>;
    }
    return <div />;
  };

  return (
    <ContentCardLink className="contest-problem-card" to={`/contests/${contest.slug}/problems/${alias}`}>
      <div className="contest-problem-card__name">
        <span data-key="name">
          {alias}. {problemName}
          {problemPoints}
        </span>
      </div>
      <div data-key="status" className="contest-problem-card__status secondary-info">
        {renderStatus()}
      </div>
      <div className="clearfix" />
    </ContentCardLink>
  );
}
