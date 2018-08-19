import { ForbiddenError } from 'modules/api/error';
import { JophielRole } from 'modules/api/jophiel/role';
import { UserWebConfig } from 'modules/api/jophiel/userWeb';
import { PutToken, PutUser } from 'modules/session/sessionReducer';
import { token, user, userJid } from 'fixtures/state';

import { loginActions } from './loginActions';
import { PutWebConfig } from '../../modules/userWebReducer';

describe('loginActions', () => {
  const authCode = 'authCode';
  const config: UserWebConfig = { role: JophielRole.User };

  let dispatch: jest.Mock<any>;
  let getState: jest.Mock<any>;

  let legacySessionAPI: jest.Mocked<any>;
  let myAPI: jest.Mocked<any>;
  let userWebAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();
    getState = jest.fn();

    legacySessionAPI = {
      logIn: jest.fn(),
      preparePostLogin: jest.fn(),
    };
    myAPI = {
      getMyself: jest.fn(),
    };
    userWebAPI = {
      getWebConfig: jest.fn(),
    };
    toastActions = {
      showToast: jest.fn(),
      showErrorToast: jest.fn(),
    };
  });

  describe('logIn()', () => {
    const { logIn } = loginActions;
    const doLogIn = async () =>
      logIn('path', 'user', 'pass')(dispatch, getState, {
        legacySessionAPI,
        myAPI,
        userWebAPI,
        toastActions,
      });

    it('calls API to logs in', async () => {
      legacySessionAPI.logIn.mockImplementation(() => Promise.resolve({ authCode, token }));
      myAPI.getMyself.mockImplementation(() => Promise.resolve<any>({ jid: userJid }));
      userWebAPI.getWebConfig.mockImplementation(() => Promise.resolve(config));

      await doLogIn();

      expect(legacySessionAPI.logIn).toHaveBeenCalledWith('user', 'pass');
    });

    describe('when the credentials is valid', () => {
      beforeEach(async () => {
        legacySessionAPI.logIn.mockImplementation(() => Promise.resolve({ authCode, token }));
        myAPI.getMyself.mockImplementation(() => Promise.resolve(user));
        userWebAPI.getWebConfig.mockImplementation(() => Promise.resolve(config));

        await doLogIn();
      });

      it('succeeds with toast', () => {
        expect(toastActions.showToast).toHaveBeenCalledWith('Welcome, user.');
      });

      it('redirects to legacy prepare post login url', () => {
        expect(legacySessionAPI.preparePostLogin).toHaveBeenCalledWith(authCode, 'path');
      });

      it('puts the session', () => {
        expect(dispatch).toHaveBeenCalledWith(PutToken.create(token));
        expect(dispatch).toHaveBeenCalledWith(PutUser.create(user));
      });

      it('puts the web config', () => {
        expect(dispatch).toHaveBeenCalledWith(PutWebConfig.create(config));
      });
    });

    describe('when the credentials is invalid', () => {
      let error: any;

      beforeEach(async () => {
        error = new ForbiddenError();
        legacySessionAPI.logIn.mockImplementation(() => {
          throw error;
        });
      });

      it('throws a more descriptive error', async () => {
        await expect(doLogIn()).rejects.toEqual(new Error('Invalid username/password.'));
      });
    });
  });
});
