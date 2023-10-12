import { Tag } from '@blueprintjs/core';

import { getVerdictDisplayName, getVerdictIntent } from '../../modules/api/gabriel/verdict';

export const VerdictTag = ({ verdictCode }) => {
  return (
    <Tag round intent={getVerdictIntent(verdictCode)}>
      {getVerdictDisplayName(verdictCode)}
    </Tag>
  );
};
