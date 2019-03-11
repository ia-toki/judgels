import * as React from 'react';
import { Tag, Intent } from '@blueprintjs/core';
import { Verdict } from 'modules/api/sandalphon/submissionBundle';

export interface VerdictTagProps {
  verdict: Verdict;
}

export const VerdictTag: React.FunctionComponent<VerdictTagProps> = ({ verdict }) => {
  let intent: Intent = Intent.NONE;
  let text = 'None';
  if (verdict === Verdict.ACCEPTED) {
    intent = Intent.SUCCESS;
    text = 'AC';
  } else if (verdict === Verdict.INTERNAL_ERROR) {
    intent = Intent.DANGER;
    text = '???';
  } else if (verdict === Verdict.OK) {
    intent = Intent.SUCCESS;
    text = 'OK';
  } else if (verdict === Verdict.PENDING_MANUAL_GRADING) {
    intent = Intent.NONE;
    text = 'Manual';
  } else if (verdict === Verdict.WRONG_ANSWER) {
    intent = Intent.DANGER;
    text = 'WA';
  }
  return <Tag intent={intent}>{text}</Tag>;
};
