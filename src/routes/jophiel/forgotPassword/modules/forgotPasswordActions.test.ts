import { forgotPasswordActions } from './forgotPasswordActions';
import { NotFoundError } from '../../../../modules/api/error';

describe('forgotPasswordActions', () => {
  let dispatch: jest.Mock<any>;
  let getState: jest.Mock<any>;

  let userAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();
    getState = jest.fn();

    userAPI = {
      requestToResetUserPassword: jest.fn(),
    };
  });

  describe('requestToReset()', () => {
    const { requestToReset } = forgotPasswordActions;
    const doRequestToReset = async () => requestToReset('email@domain.com')(dispatch, getState, { userAPI });

    it('calls API to request to reset password', async () => {
      await doRequestToReset();

      expect(userAPI.requestToResetUserPassword).toHaveBeenCalledWith('email@domain.com');
    });

    describe('when the email is not found', () => {
      beforeEach(async () => {
        userAPI.requestToResetUserPassword.mockImplementation(() => {
          throw new NotFoundError();
        });
      });

      it('throws with descriptive error', async () => {
        await expect(doRequestToReset()).rejects.toEqual(new Error('Email not found.'));
      });
    });
  });
});
