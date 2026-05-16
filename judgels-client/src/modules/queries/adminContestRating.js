import { queryOptions } from '@tanstack/react-query';

import { adminContestRatingAPI } from '../api/admin/contestRating';
import { getToken } from '../session';

export const contestsPendingRatingQueryOptions = () =>
  queryOptions({
    queryKey: ['admin', 'contest-rating', 'pending'],
    queryFn: () => adminContestRatingAPI.getContestsPendingRating(getToken()),
  });
