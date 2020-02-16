import { push } from 'connected-react-router';

import { BadRequestError } from '../../../../modules/api/error';
import { userAccountAPI } from '../../../../modules/api/jophiel/userAccount';
import * as toastActions from '../../../../modules/toast/toastActions';

export function resetPassword(emailCode: string, newPassword: string) {
  return async dispatch => {
    try {
      await userAccountAPI.resetPassword({ emailCode, newPassword });
    } catch (error) {
      if (error instanceof BadRequestError) {
        throw new Error('Invalid code.');
      } else {
        throw error;
      }
    }
    toastActions.showSuccessToast('Password has been reset.');
    dispatch(push('/login'));
  };
}
