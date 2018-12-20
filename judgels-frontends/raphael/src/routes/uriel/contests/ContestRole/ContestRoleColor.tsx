import { Intent } from '@blueprintjs/core';

import { ContestRole } from 'modules/api/uriel/contestWeb';

export const contestRoleColor = {
  [ContestRole.Admin]: Intent.DANGER,
  [ContestRole.Manager]: Intent.DANGER,
  [ContestRole.Supervisor]: Intent.WARNING,
  [ContestRole.Contestant]: Intent.PRIMARY,
};
