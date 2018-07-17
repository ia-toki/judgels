import { push } from 'react-router-redux';

import { BadRequestError } from '../../../../modules/api/error';

export const resetPasswordActions = {
  reset: (emailCode: string, newPassword: string) => {
    return async (dispatch, getState, { userAPI, toastActions }) => {
      try {
        await userAPI.resetUserPassword({ emailCode, newPassword });
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
