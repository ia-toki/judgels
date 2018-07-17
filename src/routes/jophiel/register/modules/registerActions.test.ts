import { SubmissionError } from 'redux-form';

import { registerActions } from './registerActions';
import { UserRegistrationData } from '../../../../modules/api/jophiel/user';

describe('registerActions', () => {
  let dispatch: jest.Mock<any>;
  let getState: jest.Mock<any>;

  let userAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();
    getState = jest.fn();

    userAPI = {
      usernameExists: jest.fn(),
      emailExists: jest.fn(),
      registerUser: jest.fn(),
    };
  });

  describe('register()', () => {
    const { register } = registerActions;
    const userRegistrationData: UserRegistrationData = {
      username: 'user',
      name: 'name',
      email: 'email@domain.com',
      password: 'pass',
    };
    const doRegister = async () => register(userRegistrationData)(dispatch, getState, { userAPI });

    describe('when username already exists', () => {
      beforeEach(async () => {
        userAPI.usernameExists.mockImplementation(() => Promise.resolve(true));
        userAPI.emailExists.mockImplementation(() => Promise.resolve(false));
      });

      it('throws SubmissionError', async () => {
        setTimeout(() => {
          expect(async () => {
            await doRegister();
          }).toThrow(SubmissionError);
        });
      });
    });

    describe('when email already exists', () => {
      beforeEach(async () => {
        userAPI.usernameExists.mockImplementation(() => Promise.resolve(false));
        userAPI.emailExists.mockImplementation(() => Promise.resolve(true));
      });

      it('throws SubmissionError', async () => {
        setTimeout(() => {
          expect(async () => {
            await doRegister();
          }).toThrow(SubmissionError);
        });
      });
    });

    describe('when the form is valid', () => {
      beforeEach(async () => {
        userAPI.usernameExists.mockImplementation(() => Promise.resolve(false));
        userAPI.emailExists.mockImplementation(() => Promise.resolve(false));
      });

      it('tries to register user', async () => {
        await doRegister();

        expect(userAPI.registerUser).toHaveBeenCalledWith(userRegistrationData);
      });
    });
  });
});
