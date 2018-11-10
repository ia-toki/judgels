import { push } from 'react-router-redux';

import { BadRequestError } from 'modules/api/error';
import { AppState } from 'modules/store';
import { sessionState, token } from 'fixtures/state';

import { changePasswordActions } from './changePasswordActions';

describe('changePasswordActions', () => {
  let dispatch: jest.Mock<any>;

  const getState = (): Partial<AppState> => ({ session: sessionState });

  let myUserAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    myUserAPI = {
      updateMyPassword: jest.fn(),
    };
    toastActions = {
      showSuccessToast: jest.fn(),
      showErrorToast: jest.fn(),
    };
  });

  describe('updateMyPassword()', () => {
    const { updateMyPassword } = changePasswordActions;
    const doUpdateMyPassword = async () =>
      updateMyPassword('oldPass', 'newPass')(dispatch, getState, {
        myUserAPI,
        toastActions,
      });

    it('tries to change password', async () => {
      await doUpdateMyPassword();

      expect(myUserAPI.updateMyPassword).toHaveBeenCalledWith(token, {
        oldPassword: 'oldPass',
        newPassword: 'newPass',
      });
    });

    describe('when the old password is correct', () => {
      beforeEach(async () => {
        await doUpdateMyPassword();
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
        myUserAPI.updateMyPassword.mockImplementation(() => {
          throw error;
        });
      });

      it('throws a more descriptive error', async () => {
        await expect(doUpdateMyPassword()).rejects.toEqual(new Error('Incorrect old password.'));
      });
    });
  });
});
