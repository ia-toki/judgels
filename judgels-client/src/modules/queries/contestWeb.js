import { queryOptions } from '@tanstack/react-query';

import { contestWebAPI } from '../api/uriel/contestWeb';
import { getToken } from '../session';

export const contestWebConfigQueryOptions = contestJid =>
  queryOptions({
    queryKey: ['contest', contestJid, 'web-config'],
    queryFn: () => contestWebAPI.getWebConfig(getToken(), contestJid),
    refetchInterval: 20000,
  });
