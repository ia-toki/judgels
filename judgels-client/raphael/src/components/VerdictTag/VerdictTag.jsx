import { Tag } from '@blueprintjs/core';
import { Time } from '@blueprintjs/icons';

import { getVerdictDisplayCode, getVerdictIntent, VerdictCode } from '../../modules/api/gabriel/verdict';

export const VerdictTag = ({ verdictCode }) => {
  const tag = verdictCode === VerdictCode.PND ? <Time /> : getVerdictDisplayCode(verdictCode);
  return (
    <Tag round intent={getVerdictIntent(verdictCode)}>
      {tag}
    </Tag>
  );
};
