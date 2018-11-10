import { push } from 'react-router-redux';

import { selectToken } from 'modules/session/sessionSelectors';
import { BadRequestError } from 'modules/api/error';

export const changePasswordActions = {
  updateMyPassword: (oldPassword: string, newPassword: string) => {
    return async (dispatch, getState, { myUserAPI, toastActions }) => {
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
      dispatch(push('/account/profile'));
    };
  },
};
