import { push } from 'connected-react-router';

import { selectToken } from '../../../../../modules/session/sessionSelectors';
import { BadRequestError } from '../../../../../modules/api/error';
import { myUserAPI } from '../../../../../modules/api/jophiel/myUser';
import { toastActions } from '../../../../../modules/toast/toastActions';

export function updateMyPassword(oldPassword: string, newPassword: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());

    try {
      await myUserAPI.updateMyPassword(token, { oldPassword, newPassword });
    } catch (error) {
      if (error instanceof BadRequestError) {
        throw new Error('Incorrect old password.');
      } else {
        throw error;
      }
    }

    toastActions.showSuccessToast('Password updated.');
    dispatch(push('/account/info'));
  };
}
