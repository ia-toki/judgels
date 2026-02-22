import { queryOptions } from '@tanstack/react-query';

import { contestHistoryAPI } from '../api/uriel/contestHistory';

export const contestPublicHistoryQueryOptions = username =>
  queryOptions({
    queryKey: ['contest-history', username],
    queryFn: () => contestHistoryAPI.getPublicHistory(username),
  });
