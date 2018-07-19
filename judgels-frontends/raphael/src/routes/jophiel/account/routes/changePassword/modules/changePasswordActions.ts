import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { BadRequestError } from '../../../../../../modules/api/error';
import { push } from 'react-router-redux';

export const changePasswordActions = {
  updateMyPassword: (oldPassword: string, newPassword: string) => {
    return async (dispatch, getState, { myAPI, toastActions }) => {
      const token = selectToken(getState());

      try {
        await myAPI.updateMyPassword(token, { oldPassword, newPassword });
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
