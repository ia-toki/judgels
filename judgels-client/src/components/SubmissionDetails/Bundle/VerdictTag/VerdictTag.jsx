import { Intent, Tag } from '@blueprintjs/core';
import { Follower, Time } from '@blueprintjs/icons';

import { Verdict } from '../../../../modules/api/sandalphon/submissionBundle';

const verdictIntentMap = {
  [Verdict.ACCEPTED]: Intent.SUCCESS,
  [Verdict.INTERNAL_ERROR]: Intent.DANGER,
  [Verdict.OK]: Intent.SUCCESS,
  [Verdict.PENDING_REGRADE]: Intent.NONE,
  [Verdict.PENDING_MANUAL_GRADING]: Intent.NONE,
  [Verdict.WRONG_ANSWER]: Intent.DANGER,
};

const verdictDisplayName = {
  [Verdict.ACCEPTED]: 'Correct',
  [Verdict.INTERNAL_ERROR]: '???',
  [Verdict.OK]: 'OK',
  [Verdict.PENDING_REGRADE]: 'Pending',
  [Verdict.PENDING_MANUAL_GRADING]: 'Manual',
  [Verdict.WRONG_ANSWER]: 'Incorrect',
};

export function VerdictTag({ verdict }) {
  const intent = verdictIntentMap[verdict] || Intent.NONE;
  let displayName = verdictDisplayName[verdict] || verdict;
  if (verdict === Verdict.PENDING_REGRADE) {
    displayName = <Time />;
  } else if (verdict === Verdict.PENDING_MANUAL_GRADING) {
    displayName = <Follower />;
  }
  return (
    <Tag round intent={intent}>
      {displayName}
    </Tag>
  );
}
