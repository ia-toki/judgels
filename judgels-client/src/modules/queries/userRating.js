import { userRatingAPI } from '../api/userRating';
import { contestsPendingRatingQueryOptions } from '../queries/contestRating';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const updateUserRatingsMutationOptions = {
  mutationFn: data => userRatingAPI.updateRatings(getToken(), data),
  onSuccess: () => {
    queryClient.invalidateQueries(contestsPendingRatingQueryOptions());
  },
};
