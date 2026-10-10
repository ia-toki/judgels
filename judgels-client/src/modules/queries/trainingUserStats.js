import { queryOptions } from '@tanstack/react-query';

import { trainingUserStatsAPI } from '../api/trainingUserStats';

export const trainingUserStatsQueryOptions = username =>
  queryOptions({
    queryKey: ['training', 'user-stats', username],
    queryFn: () => trainingUserStatsAPI.getUserStats(username),
  });

export const trainingTopUserStatsQueryOptions = params => {
  const { page, pageSize } = params || {};
  return queryOptions({
    queryKey: ['training', 'top-user-stats', ...(params ? [params] : [])],
    queryFn: () => trainingUserStatsAPI.getTopUserStats(page, pageSize),
  });
};
