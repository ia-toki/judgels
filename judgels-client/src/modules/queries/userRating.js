import { userRatingAPI } from '../api/jophiel/userRating';
import { contestsPendingRatingQueryOptions } from '../queries/contestRating';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const updateRatingsMutationOptions = {
  mutationFn: data => userRatingAPI.updateRatings(getToken(), data),
  onSuccess: () => {
    queryClient.invalidateQueries(contestsPendingRatingQueryOptions());
  },
};
