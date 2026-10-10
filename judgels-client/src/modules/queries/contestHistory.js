import { queryOptions } from '@tanstack/react-query';

import { contestHistoryAPI } from '../api/contestHistory';

export const contestPublicHistoryQueryOptions = username =>
  queryOptions({
    queryKey: ['contest-history', 'public', username],
    queryFn: () => contestHistoryAPI.getPublicHistory(username),
  });
