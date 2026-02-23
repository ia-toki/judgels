import { queryOptions } from '@tanstack/react-query';

import { statsAPI } from '../api/jerahmeel/stats';

export const userStatsQueryOptions = username =>
  queryOptions({
    queryKey: ['user-stats', username],
    queryFn: () => statsAPI.getUserStats(username),
  });

export const topUserStatsQueryOptions = params => {
  const { page, pageSize } = params || {};
  return queryOptions({
    queryKey: ['user-stats', 'top', ...(params ? [params] : [])],
    queryFn: () => statsAPI.getTopUserStats(page, pageSize),
  });
};
