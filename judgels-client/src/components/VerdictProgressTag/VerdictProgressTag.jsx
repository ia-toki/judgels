import { Tag } from '@blueprintjs/core';
import classNames from 'classnames';

import { VerdictCode, getVerdictIntent } from '../../modules/api/gabriel/verdict';

import './VerdictProgressTag.scss';

export const VerdictProgressTag = ({ className, verdict, score }) => {
  const intent = getVerdictIntent(verdict);

  if (verdict === VerdictCode.PND) {
    return (
      <Tag className={classNames('verdict-progress-tag', className)} intent={intent}>
        not attempted
      </Tag>
    );
  }

  return (
    <Tag className={classNames('verdict-progress-tag', className)} intent={intent}>
      {verdict} {score}
    </Tag>
  );
};
