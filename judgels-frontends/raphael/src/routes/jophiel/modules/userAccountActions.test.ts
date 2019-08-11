import { userAccountActions } from './userAccountActions';

describe('userAccountActions', () => {
  let dispatch: jest.Mock<any>;
  let getState: jest.Mock<any>;

  let userAccountAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();
    getState = jest.fn();

    userAccountAPI = {
      registerUser: jest.fn(),
      resendActivationEmail: jest.fn(),
    };
    toastActions = {
      showToast: jest.fn(),
    };
  });

  describe('resendActivationEmail()', () => {
    const { resendActivationEmail } = userAccountActions;
    const email = 'email@domain.com';
    const doResendActivationEmail = async () =>
      resendActivationEmail(email)(dispatch, getState, { userAccountAPI, toastActions });

    beforeEach(async () => {
      doResendActivationEmail();
    });

    it('calls API to resend activation email', async () => {
      expect(userAccountAPI.resendActivationEmail).toHaveBeenCalledWith(email);
    });

    it('succeeds with toast', () => {
      expect(toastActions.showToast).toHaveBeenCalledWith('Email has been sent');
    });
  });
});
