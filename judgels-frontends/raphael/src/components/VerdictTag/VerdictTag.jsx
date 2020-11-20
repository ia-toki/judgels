import { Icon, Tag } from '@blueprintjs/core';
import * as React from 'react';

import { getVerdictDisplayCode, getVerdictIntent, VerdictCode } from '../../modules/api/gabriel/verdict';

export const VerdictTag = ({ verdictCode }) => {
  const tag = verdictCode === VerdictCode.PND ? <Icon icon="time" /> : getVerdictDisplayCode(verdictCode);
  return (
    <Tag round intent={getVerdictIntent(verdictCode)}>
      {tag}
    </Tag>
  );
};
