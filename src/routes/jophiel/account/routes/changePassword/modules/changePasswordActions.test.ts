import { push } from 'react-router-redux';

import { changePasswordActions } from './changePasswordActions';
import { BadRequestError } from '../../../../../../modules/api/error';
import { AppState } from '../../../../../../modules/store';
import { sessionState, token } from '../../../../../../fixtures/state';

describe('changePasswordActions', () => {
  let dispatch: jest.Mock<any>;

  const getState = (): Partial<AppState> => ({ session: sessionState });

  let userAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    userAPI = {
      updateMyPassword: jest.fn(),
    };
    toastActions = {
      showSuccessToast: jest.fn(),
      showErrorToast: jest.fn(),
    };
  });

  describe('changePassword()', () => {
    const { changePassword } = changePasswordActions;
    const doChangePassword = async () =>
      changePassword('oldPass', 'newPass')(dispatch, getState, {
        userAPI,
        toastActions,
      });

    it('tries to change password', async () => {
      await doChangePassword();

      expect(userAPI.updateMyPassword).toHaveBeenCalledWith(token, {
        oldPassword: 'oldPass',
        newPassword: 'newPass',
      });
    });

    describe('when the old password is correct', () => {
      beforeEach(async () => {
        await doChangePassword();
      });

      it('succeeds with toast', () => {
        expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Password updated.');
      });

      it('redirects to /profile', () => {
        expect(dispatch).toHaveBeenCalledWith(push('/account/profile'));
      });
    });

    describe('when the old password is incorrect', () => {
      let error: any;

      beforeEach(async () => {
        error = new BadRequestError();
        userAPI.updateMyPassword.mockImplementation(() => {
          throw error;
        });
      });

      it('throws a more descriptive error', async () => {
        await expect(doChangePassword()).rejects.toEqual(new Error('Incorrect old password.'));
      });
    });
  });
});
