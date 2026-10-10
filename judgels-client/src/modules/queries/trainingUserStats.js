import { queryOptions } from '@tanstack/react-query';

import { trainingUserStatsAPI } from '../api/trainingUserStats';

export const userStatsQueryOptions = username =>
  queryOptions({
    queryKey: ['user-stats', username],
    queryFn: () => trainingUserStatsAPI.getUserStats(username),
  });

export const topUserStatsQueryOptions = params => {
  const { page, pageSize } = params || {};
  return queryOptions({
    queryKey: ['user-stats', 'top', ...(params ? [params] : [])],
    queryFn: () => trainingUserStatsAPI.getTopUserStats(page, pageSize),
  });
};
