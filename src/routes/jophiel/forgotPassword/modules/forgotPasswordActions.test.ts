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
      requestToResetUserPassword: jest.fn(),
    };
  });

  describe('requestToReset()', () => {
    const { requestToReset } = forgotPasswordActions;
    const doRequestToReset = async () => requestToReset('email@domain.com')(dispatch, getState, { userAccountAPI });

    it('calls API to request to reset password', async () => {
      await doRequestToReset();

      expect(userAccountAPI.requestToResetUserPassword).toHaveBeenCalledWith('email@domain.com');
    });

    describe('when the email is not found', () => {
      beforeEach(async () => {
        userAccountAPI.requestToResetUserPassword.mockImplementation(() => {
          throw new NotFoundError();
        });
      });

      it('throws with descriptive error', async () => {
        await expect(doRequestToReset()).rejects.toEqual(new Error('Email not found.'));
      });
    });
  });
});
