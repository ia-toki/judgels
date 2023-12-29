import { ProgressBar as BlueprintProgressBar, Intent } from '@blueprintjs/core';
import classNames from 'classnames';

import { getVerdictIntent } from '../../modules/api/gabriel/verdict';

import './ProgressBar.scss';

export function ProgressBar({ className, num, denom, verdict }) {
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
      className={classNames('progress-bar', className)}
      animate={false}
      stripes={false}
      intent={intent}
      value={num / denom}
    />
  );
}
