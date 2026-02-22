import { queryOptions } from '@tanstack/react-query';

import { statsAPI } from '../api/jerahmeel/stats';

export const userStatsQueryOptions = username =>
  queryOptions({
    queryKey: ['user-stats', username],
    queryFn: () => statsAPI.getUserStats(username),
  });
