import { Intent, Tag } from '@blueprintjs/core';
import * as React from 'react';

export interface ProgressTagProps {
  className?: string;
  num: number;
  denom: number;
  children: any;
}

export const ProgressTag = (props: ProgressTagProps) => {
  const { className, num, denom, children } = props;
  if (denom === 0) {
    return null;
  }

  let intent: Intent;
  if (num === denom) {
    intent = Intent.SUCCESS;
  } else if (num === 0) {
    intent = Intent.NONE;
  } else if (num * 3 <= denom) {
    intent = Intent.DANGER;
  } else {
    intent = Intent.WARNING;
  }
  return (
    <Tag className={className} intent={intent}>
      {children}
    </Tag>
  );
};
