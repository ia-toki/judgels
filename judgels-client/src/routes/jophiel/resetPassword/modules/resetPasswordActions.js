import { BadRequestError } from '../../../../modules/api/error';
import { userAccountAPI } from '../../../../modules/api/jophiel/userAccount';
import { getNavigationRef } from '../../../../modules/navigation/navigationRef';

import * as toastActions from '../../../../modules/toast/toastActions';

export function resetPassword(emailCode, newPassword) {
  return async () => {
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
    getNavigationRef().push('/login');
  };
}
