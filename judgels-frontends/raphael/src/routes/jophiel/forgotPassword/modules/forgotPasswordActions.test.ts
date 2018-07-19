import { forgotPasswordActions } from './forgotPasswordActions';
import { NotFoundError } from '../../../../modules/api/error';

describe('forgotPasswordActions', () => {
  let dispatch: jest.Mock<any>;
  let getState: jest.Mock<any>;

  let userAccountAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();
    getState = jest.fn();

    userAccountAPI = {
      requestToResetPassword: jest.fn(),
    };
  });

  describe('requestToResetPassword()', () => {
    const { requestToResetPassword } = forgotPasswordActions;
    const doRequestToResetPassword = async () =>
      requestToResetPassword('email@domain.com')(dispatch, getState, { userAccountAPI });

    it('calls API to request to reset password', async () => {
      await doRequestToResetPassword();

      expect(userAccountAPI.requestToResetPassword).toHaveBeenCalledWith('email@domain.com');
    });

    describe('when the email is not found', () => {
      beforeEach(async () => {
        userAccountAPI.requestToResetPassword.mockImplementation(() => {
          throw new NotFoundError();
        });
      });

      it('throws with descriptive error', async () => {
        await expect(doRequestToResetPassword()).rejects.toEqual(new Error('Email not found.'));
      });
    });
  });
});
