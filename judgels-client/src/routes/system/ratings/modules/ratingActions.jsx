import { userRatingAPI } from '../../../../modules/api/jophiel/userRating';
import { contestRatingAPI } from '../../../../modules/api/uriel/contestRating';
import { selectToken } from '../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../modules/toast/toastActions';

export function getContestsPendingRating() {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestRatingAPI.getContestsPendingRating(token);
  };
}

export function updateRatings(data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await userRatingAPI.updateRatings(token, data);
    toastActions.showSuccessToast('Ratings updated.');
  };
}
