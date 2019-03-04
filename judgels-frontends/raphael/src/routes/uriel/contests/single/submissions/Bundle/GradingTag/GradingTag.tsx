import React, { FunctionComponent } from 'react';
import { Tag, Intent } from '@blueprintjs/core';
import { Grading, Verdict } from 'modules/api/sandalphon/submissionBundle';

export interface GradingTagProps {
  grading?: Grading;
}

export const GradingTag: FunctionComponent<GradingTagProps> = ({ grading }) => {
  let intent: Intent = Intent.NONE;
  let text = 'None';
  if (grading) {
    if (grading.verdict === Verdict.ACCEPTED) {
      intent = Intent.SUCCESS;
      text = 'AC';
    } else if (grading.verdict === Verdict.GRADING_NOT_NEEDED) {
      intent = Intent.NONE;
      text = 'Not Needed';
    } else if (grading.verdict === Verdict.INTERNAL_ERROR) {
      intent = Intent.DANGER;
      text = 'IE';
    } else if (grading.verdict === Verdict.NO_ANSWER) {
      intent = Intent.NONE;
      text = 'No Ans';
    } else if (grading.verdict === Verdict.OK) {
      intent = Intent.SUCCESS;
      text = 'OK';
    } else if (grading.verdict === Verdict.PENDING) {
      intent = Intent.NONE;
      text = 'Pending';
    } else if (grading.verdict === Verdict.PENDING_MANUAL_GRADING) {
      intent = Intent.NONE;
      text = 'Pending Manual';
    } else if (grading.verdict === Verdict.WRONG_ANSWER) {
      intent = Intent.DANGER;
      text = 'WA';
    }
  }
  return <Tag intent={intent}>{text}</Tag>;
};
