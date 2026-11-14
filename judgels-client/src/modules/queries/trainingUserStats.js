import { queryOptions } from '@tanstack/react-query';

import { trainingUserStatsAPI } from '../api/trainingUserStats';

export const trainingUserStatsQueryOptions = username =>
  queryOptions({
    queryKey: ['training', 'stats', 'users', { username }],
    queryFn: () => trainingUserStatsAPI.getUserStats(username),
  });
