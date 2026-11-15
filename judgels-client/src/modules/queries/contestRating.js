import { queryOptions } from '@tanstack/react-query';

import { contestRatingAPI } from '../api/contestRating';
import { getToken } from '../session';

export const contestsPendingRatingQueryOptions = () => {
  return queryOptions({
    queryKey: ['contest-rating', 'pending'],
    queryFn: () => contestRatingAPI.getContestsPendingRating(getToken()),
  });
};
