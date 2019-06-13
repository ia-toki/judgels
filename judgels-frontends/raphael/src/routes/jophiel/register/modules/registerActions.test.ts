import { SubmissionError } from 'redux-form';

import { UserRegistrationData } from 'modules/api/jophiel/userAccount';

import { registerActions } from './registerActions';

describe('registerActions', () => {
  let dispatch: jest.Mock<any>;
  let getState: jest.Mock<any>;

  let userSearchAPI: jest.Mocked<any>;
  let userAccountAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();
    getState = jest.fn();

    userSearchAPI = {
      usernameExists: jest.fn(),
      emailExists: jest.fn(),
    };
    userAccountAPI = {
      registerUser: jest.fn(),
      resendActivationEmail: jest.fn(),
    };
    toastActions = {
      showToast: jest.fn(),
    };
  });

  describe('registerUser()', () => {
    const { registerUser } = registerActions;
    const userRegistrationData: UserRegistrationData = {
      username: 'user',
      name: 'name',
      email: 'email@domain.com',
      password: 'pass',
    };
    const doRegisterUser = async () =>
      registerUser(userRegistrationData)(dispatch, getState, { userSearchAPI, userAccountAPI });

    describe('when username already exists', () => {
      beforeEach(async () => {
        userSearchAPI.usernameExists.mockImplementation(() => Promise.resolve(true));
        userSearchAPI.emailExists.mockImplementation(() => Promise.resolve(false));
      });

      it('throws SubmissionError', async () => {
        await expect(doRegisterUser()).rejects.toBeInstanceOf(SubmissionError);
      });
    });

    describe('when email already exists', () => {
      beforeEach(async () => {
        userSearchAPI.usernameExists.mockImplementation(() => Promise.resolve(false));
        userSearchAPI.emailExists.mockImplementation(() => Promise.resolve(true));
      });

      it('throws SubmissionError', async () => {
        await expect(doRegisterUser()).rejects.toBeInstanceOf(SubmissionError);
      });
    });

    describe('when the form is valid', () => {
      beforeEach(async () => {
        userSearchAPI.usernameExists.mockImplementation(() => Promise.resolve(false));
        userSearchAPI.emailExists.mockImplementation(() => Promise.resolve(false));

        await doRegisterUser();
      });

      it('tries to register user', async () => {
        expect(userAccountAPI.registerUser).toHaveBeenCalledWith(userRegistrationData);
      });
    });
  });

  describe('resendActivationEmail()', () => {
    const { resendActivationEmail } = registerActions;
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
