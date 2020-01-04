import { Tag } from '@blueprintjs/core';
import * as React from 'react';

import { getVerdictIntent, VerdictCode } from '../../modules/api/gabriel/verdict';

export interface VerdictProgressTagProps {
  className?: string;
  verdict: string;
  score: number;
}

export const VerdictProgressTag = (props: VerdictProgressTagProps) => {
  const { className, verdict, score } = props;
  const intent = getVerdictIntent(verdict);

  if (verdict === VerdictCode.PND) {
    return (
      <Tag className={className} intent={intent}>
        not tried yet
      </Tag>
    );
  }

  return (
    <Tag className={className} intent={intent}>
      {verdict} {score}
    </Tag>
  );
};
