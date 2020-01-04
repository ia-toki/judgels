import { Tag } from '@blueprintjs/core';
import * as React from 'react';

import { getVerdictIntent } from '../../modules/api/gabriel/verdict';

export interface VerdictProgressTagProps {
  className?: string;
  verdict: string;
  score: number;
}

export const VerdictProgressTag = (props: VerdictProgressTagProps) => {
  const { className, verdict, score } = props;
  const intent = getVerdictIntent(verdict);

  return (
    <Tag className={className} intent={intent}>
      {verdict} {score}
    </Tag>
  );
};
