import * as React from 'react';
import { Tag, Intent, Icon } from '@blueprintjs/core';
import { Verdict } from 'modules/api/sandalphon/submissionBundle';

export interface VerdictTagProps {
  verdict: Verdict;
}

const verdictIntentMap: { [verdict: string]: Intent } = {
  [Verdict.ACCEPTED]: Intent.SUCCESS,
  [Verdict.GRADING_NOT_NEEDED]: Intent.NONE,
  [Verdict.INTERNAL_ERROR]: Intent.DANGER,
  [Verdict.NO_ANSWER]: Intent.NONE,
  [Verdict.OK]: Intent.SUCCESS,
  [Verdict.PENDING]: Intent.NONE,
  [Verdict.PENDING_MANUAL_GRADING]: Intent.NONE,
  [Verdict.WRONG_ANSWER]: Intent.DANGER,
};

const verdictDisplayCode: { [verdict: string]: string | React.ReactNode } = {
  [Verdict.ACCEPTED]: 'AC',
  [Verdict.GRADING_NOT_NEEDED]: 'Not Needed',
  [Verdict.INTERNAL_ERROR]: 'IE',
  [Verdict.NO_ANSWER]: 'No Ans',
  [Verdict.OK]: 'OK',
  [Verdict.PENDING]: <Icon icon="time" />,
  [Verdict.PENDING_MANUAL_GRADING]: 'MNL',
  [Verdict.WRONG_ANSWER]: 'WA',
};

export const VerdictTag: React.FunctionComponent<VerdictTagProps> = ({ verdict }) => {
  const intent = verdictIntentMap[verdict] || Intent.NONE;
  const displayCode = verdictDisplayCode[verdict] || verdict;
  return (
    <Tag round intent={intent}>
      {displayCode}
    </Tag>
  );
};
