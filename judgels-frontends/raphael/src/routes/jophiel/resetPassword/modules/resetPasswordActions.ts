import { push } from 'connected-react-router';

import { BadRequestError } from '../../../../modules/api/error';

export const resetPasswordActions = {
  resetPassword: (emailCode: string, newPassword: string) => {
    return async (dispatch, getState, { userAccountAPI, toastActions }) => {
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
  },
};
