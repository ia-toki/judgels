import { SubmissionError } from 'redux-form';

import { UserRegistrationData } from 'modules/api/jophiel/userAccount';

import { registerActions } from './registerActions';

describe('registerActions', () => {
  let dispatch: jest.Mock<any>;
  let getState: jest.Mock<any>;

  let userAPI: jest.Mocked<any>;
  let userAccountAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();
    getState = jest.fn();

    userAPI = {
      usernameExists: jest.fn(),
      emailExists: jest.fn(),
    };
    userAccountAPI = {
      registerUser: jest.fn(),
    };
  });

  describe('registerUser)', () => {
    const { registerUser } = registerActions;
    const userRegistrationData: UserRegistrationData = {
      username: 'user',
      name: 'name',
      email: 'email@domain.com',
      password: 'pass',
    };
    const doRegisterUser = async () =>
      registerUser(userRegistrationData)(dispatch, getState, { userAPI, userAccountAPI });

    describe('when username already exists', () => {
      beforeEach(async () => {
        userAPI.usernameExists.mockImplementation(() => Promise.resolve(true));
        userAPI.emailExists.mockImplementation(() => Promise.resolve(false));
      });

      it('throws SubmissionError', async () => {
        await expect(doRegisterUser()).rejects.toBeInstanceOf(SubmissionError);
      });
    });

    describe('when email already exists', () => {
      beforeEach(async () => {
        userAPI.usernameExists.mockImplementation(() => Promise.resolve(false));
        userAPI.emailExists.mockImplementation(() => Promise.resolve(true));
      });

      it('throws SubmissionError', async () => {
        await expect(doRegisterUser()).rejects.toBeInstanceOf(SubmissionError);
      });
    });

    describe('when the form is valid', () => {
      beforeEach(async () => {
        userAPI.usernameExists.mockImplementation(() => Promise.resolve(false));
        userAPI.emailExists.mockImplementation(() => Promise.resolve(false));

        await doRegisterUser();
      });

      it('tries to register user', async () => {
        expect(userAccountAPI.registerUser).toHaveBeenCalledWith(userRegistrationData);
      });
    });
  });
});
