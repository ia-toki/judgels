import { Intent, ProgressBar as BlueprintProgressBar } from '@blueprintjs/core';
import * as React from 'react';

import { getVerdictIntent } from '../../modules/api/gabriel/verdict';

export function ProgressBar({ num, denom, verdict }) {
  if (denom === 0) {
    return null;
  }

  let intent;
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
}
