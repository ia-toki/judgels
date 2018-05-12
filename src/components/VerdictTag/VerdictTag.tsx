import { Tag } from '@blueprintjs/core';
import * as React from 'react';

import { getVerdictDisplayCode, getVerdictIntent } from '../../modules/api/gabriel/verdict';

export interface VerdictTagProps {
  verdictCode: string;
}

export const VerdictTag = (props: VerdictTagProps) => {
  const { verdictCode } = props;
  return (
    <Tag round intent={getVerdictIntent(verdictCode)}>
      {getVerdictDisplayCode(verdictCode)}
    </Tag>
  );
};
