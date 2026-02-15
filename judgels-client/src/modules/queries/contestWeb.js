import { queryOptions } from '@tanstack/react-query';

import { contestWebAPI } from '../api/uriel/contestWeb';
import { getToken } from '../session';

export const contestWebConfigQueryOptions = contestSlug =>
  queryOptions({
    queryKey: ['contest-by-slug', contestSlug, 'web-config'],
    queryFn: () => contestWebAPI.getWebConfigBySlug(getToken(), contestSlug),
    refetchInterval: 20000,
  });
