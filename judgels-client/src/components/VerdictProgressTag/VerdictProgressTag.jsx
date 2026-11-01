import { Tag } from '@blueprintjs/core';

import { VerdictCode, getVerdictIntent } from '../../modules/api/gradingVerdict';

export function VerdictProgressTag({ className, verdict, score }) {
  const intent = getVerdictIntent(verdict);

  if (verdict === VerdictCode.PND) {
    return (
      <Tag className={className} intent={intent}>
        not attempted
      </Tag>
    );
  }

  return (
    <Tag className={className} intent={intent}>
      {verdict} {score}
    </Tag>
  );
}
