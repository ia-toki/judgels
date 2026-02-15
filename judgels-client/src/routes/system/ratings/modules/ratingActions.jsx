import { userRatingAPI } from '../../../../modules/api/jophiel/userRating';
import { contestRatingAPI } from '../../../../modules/api/uriel/contestRating';
import { getToken } from '../../../../modules/session';

import * as toastActions from '../../../../modules/toast/toastActions';

export async function getContestsPendingRating() {
  const token = getToken();
  return await contestRatingAPI.getContestsPendingRating(token);
}

export async function updateRatings(data) {
  const token = getToken();
  await userRatingAPI.updateRatings(token, data);
  toastActions.showSuccessToast('Ratings updated.');
}
