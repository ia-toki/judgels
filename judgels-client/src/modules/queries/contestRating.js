import { queryOptions } from '@tanstack/react-query';

import { contestRatingAPI } from '../api/contestRating';
import { getToken } from '../session';

export const contestsPendingRatingQueryOptions = () => {
  return queryOptions({
    queryKey: ['contests-pending-rating'],
    queryFn: () => contestRatingAPI.getContestsPendingRating(getToken()),
  });
};
