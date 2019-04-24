import { Tag, Intent, Icon } from '@blueprintjs/core';
import * as React from 'react';

import { Verdict } from 'modules/api/sandalphon/submissionBundle';

export interface VerdictTagProps {
  verdict: Verdict;
}

const verdictIntentMap: { [verdict: string]: Intent } = {
  [Verdict.ACCEPTED]: Intent.SUCCESS,
  [Verdict.INTERNAL_ERROR]: Intent.DANGER,
  [Verdict.OK]: Intent.SUCCESS,
  [Verdict.PENDING_REGRADE]: Intent.NONE,
  [Verdict.PENDING_MANUAL_GRADING]: Intent.NONE,
  [Verdict.WRONG_ANSWER]: Intent.DANGER,
};

const verdictDisplayCode: { [verdict: string]: string | React.ReactNode } = {
  [Verdict.ACCEPTED]: 'AC',
  [Verdict.INTERNAL_ERROR]: '???',
  [Verdict.OK]: 'OK',
  [Verdict.PENDING_REGRADE]: 'PENDING',
  [Verdict.PENDING_MANUAL_GRADING]: 'MANUAL',
  [Verdict.WRONG_ANSWER]: 'WA',
};

export const VerdictTag: React.FunctionComponent<VerdictTagProps> = ({ verdict }) => {
  const intent = verdictIntentMap[verdict] || Intent.NONE;
  let displayCode = verdictDisplayCode[verdict] || verdict;
  if (verdict === Verdict.PENDING_REGRADE) {
    displayCode = <Icon icon="time" />;
  } else if (verdict === Verdict.PENDING_MANUAL_GRADING) {
    displayCode = <Icon icon="follower" />;
  }
  return (
    <Tag round intent={intent}>
      {displayCode}
    </Tag>
  );
};
