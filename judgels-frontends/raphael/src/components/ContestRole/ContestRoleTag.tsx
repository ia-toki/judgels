import { Intent, Tag } from '@blueprintjs/core';
import * as React from 'react';

import { ContestRole } from 'modules/api/uriel/contestWeb';

const contestRoleColor = {
  [ContestRole.Admin]: Intent.DANGER,
  [ContestRole.Manager]: Intent.DANGER,
  [ContestRole.Supervisor]: Intent.WARNING,
  [ContestRole.Contestant]: Intent.PRIMARY,
};

export interface ContestRoleTagProps {
  role?: ContestRole;
}

export const ContestRoleTag = (props: ContestRoleTagProps) => {
  if (!props.role || !contestRoleColor[props.role]) {
    return null;
  }
  return <Tag intent={contestRoleColor[props.role]}>{props.role}</Tag>;
};
