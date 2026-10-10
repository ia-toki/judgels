import { queryOptions } from '@tanstack/react-query';

import { trainingStatsAPI } from '../api/trainingUserStats';

export const userStatsQueryOptions = username =>
  queryOptions({
    queryKey: ['user-stats', username],
    queryFn: () => trainingStatsAPI.getUserStats(username),
  });

export const topUserStatsQueryOptions = params => {
  const { page, pageSize } = params || {};
  return queryOptions({
    queryKey: ['user-stats', 'top', ...(params ? [params] : [])],
    queryFn: () => trainingStatsAPI.getTopUserStats(page, pageSize),
  });
};
