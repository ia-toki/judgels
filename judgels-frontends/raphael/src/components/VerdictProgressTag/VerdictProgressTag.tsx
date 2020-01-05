import { Tag } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import { getVerdictIntent, VerdictCode } from '../../modules/api/gabriel/verdict';

import './VerdictProgressTag.css';

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
      <Tag className={classNames('verdict-progress-tag', className)} intent={intent}>
        not tried
      </Tag>
    );
  }

  return (
    <Tag className={classNames('verdict-progress-tag', className)} intent={intent}>
      {verdict} {score}
    </Tag>
  );
};
