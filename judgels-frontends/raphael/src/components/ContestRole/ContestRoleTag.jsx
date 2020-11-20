import { Intent, Tag } from '@blueprintjs/core';
import * as React from 'react';

import { ContestRole } from '../../modules/api/uriel/contestWeb';

const contestRoleColor = {
  [ContestRole.Admin]: Intent.DANGER,
  [ContestRole.Manager]: Intent.DANGER,
  [ContestRole.Supervisor]: Intent.WARNING,
  [ContestRole.Contestant]: Intent.PRIMARY,
};

export function ContestRoleTag({ role }) {
  if (!role || !contestRoleColor[role]) {
    return null;
  }
  return <Tag intent={contestRoleColor[role]}>{role}</Tag>;
}
