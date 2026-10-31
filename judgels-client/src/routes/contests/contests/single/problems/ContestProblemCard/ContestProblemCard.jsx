import { Intent, Tag } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';

import { ContentRow } from '../../../../../../components/ContentRow/ContentRow';
import { ContestProblemStatus } from '../../../../../../modules/api/contestProblem';

export function ContestProblemCard({
  contest,
  problem: { alias, points, status, submissionsLimit },
  problemName,
  totalSubmissions,
}) {
  const isClosed = status === ContestProblemStatus.Closed;
  const problemPoints = points === null || points === undefined ? null : <span>{points} points</span>;

  const renderStatus = () => {
    if (isClosed) {
      return <Tag intent={Intent.DANGER}>CLOSED</Tag>;
    }
    if (!submissionsLimit) {
      return problemPoints;
    }
    return (
      <Flex alignItems="center" gap={2}>
        <Tag>{submissionsLimit - totalSubmissions} submissions left</Tag>
        {problemPoints}
      </Flex>
    );
  };

  return (
    <ContentRow
      to={`/contests/${contest.slug}/problems/${alias}`}
      title={`${alias}. ${problemName}`}
      meta={isClosed ? problemPoints : null}
      status={renderStatus()}
    />
  );
}
