import { Intent, Tag } from '@blueprintjs/core';
import { Circle, Confirm, Dashboard } from '@blueprintjs/icons';

import { VerdictCode } from '../../modules/api/gabriel/verdict';

export const ChapterProblemProgressTag = ({ verdict }) => {
  if (verdict === VerdictCode.AC) {
    return (
      <Tag intent={Intent.SUCCESS}>
        🎉 solved&nbsp;&nbsp;
        <Confirm />
      </Tag>
    );
  } else if (verdict === VerdictCode.PND) {
    return (
      <Tag intent={Intent.NONE}>
        not started&nbsp;&nbsp;
        <Circle />
      </Tag>
    );
  } else {
    return (
      <Tag intent={Intent.PRIMARY}>
        in progress&nbsp;&nbsp;
        <Dashboard />
      </Tag>
    );
  }
};
