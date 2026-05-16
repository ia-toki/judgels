import { adminUserRatingAPI } from '../api/admin/userRating';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const updateRatingsMutationOptions = {
  mutationFn: data => adminUserRatingAPI.updateRatings(getToken(), data),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['admin', 'contest-rating', 'pending'] });
  },
};
