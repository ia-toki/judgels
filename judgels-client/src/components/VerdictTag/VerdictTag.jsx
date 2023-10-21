import { Tag } from '@blueprintjs/core';

import { getVerdictDisplayName, getVerdictIntent } from '../../modules/api/gabriel/verdict';

import './VerdictTag.scss';

export const VerdictTag = ({ verdictCode, blank, square }) => {
  return (
    <Tag className="verdict-tag" round={!square} intent={getVerdictIntent(verdictCode)}>
      {blank ? <>&nbsp;&nbsp;</> : getVerdictDisplayName(verdictCode)}
    </Tag>
  );
};
