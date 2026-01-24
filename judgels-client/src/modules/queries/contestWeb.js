import { queryOptions } from '@tanstack/react-query';

import { contestWebAPI } from '../api/uriel/contestWeb';

export const contestWebConfigQueryOptions = (token, contestSlug) =>
  queryOptions({
    queryKey: ['contest-by-slug', contestSlug, 'web-config'],
    queryFn: () => contestWebAPI.getWebConfigBySlug(token, contestSlug),
    refetchInterval: 20000,
  });
