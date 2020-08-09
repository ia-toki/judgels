import { Intent, ProgressBar as BlueprintProgressBar } from '@blueprintjs/core';
import * as React from 'react';

import { getVerdictIntent } from '../../modules/api/gabriel/verdict';

export interface ProgressBarProps {
  num: number;
  denom: number;
  verdict?: string;
}

export const ProgressBar = (props: ProgressBarProps) => {
  const { num, denom, verdict } = props;
  if (denom === 0) {
    return null;
  }

  let intent: Intent;
  if (verdict) {
    intent = getVerdictIntent(verdict);
  } else if (num === denom) {
    intent = Intent.SUCCESS;
  } else {
    intent = Intent.PRIMARY;
  }
  return (
    <BlueprintProgressBar
      className="progress-bar"
      animate={false}
      stripes={false}
      intent={intent}
      value={num / denom}
    />
  );
};
