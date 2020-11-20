import { Intent, Tag } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import './ProgressTag.css';

export function ProgressTag({ className, large, num, denom, children }) {
  if (denom === 0) {
    return null;
  }

  let intent;
  if (num === denom) {
    intent = Intent.SUCCESS;
  } else if (num === 0) {
    intent = Intent.NONE;
  } else {
    intent = Intent.PRIMARY;
  }
  return (
    <Tag large={!!large} className={classNames('progress-tag', className)} intent={intent}>
      {children}
    </Tag>
  );
}
