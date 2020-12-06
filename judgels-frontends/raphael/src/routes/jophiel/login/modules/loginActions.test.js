import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { JophielRole } from '../../../../modules/api/jophiel/role';
import { PutToken, PutUser } from '../../../../modules/session/sessionReducer';
import { nockJophiel } from '../../../../utils/nock';
import * as loginActions from './loginActions';
import { PutWebConfig } from '../../modules/userWebReducer';

const usernameOrEmail = 'user';
const password = 'password';
const authCode = 'authCode';
const userJid = 'userJid';
const token = 'token123';
const user = { jid: userJid, username: usernameOrEmail, email: 'email' };
const config = { role: { jophiel: JophielRole.User } };
const mockStore = configureMockStore([thunk]);

describe('loginActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('logIn()', () => {
    describe('when the credentials is valid', () => {
      it('succeeds', async () => {
        nockJophiel()
          .post(`/session/login`, { usernameOrEmail, password })
          .reply(200, { authCode, token });

        nockJophiel()
          .options(`/users/me`)
          .reply(200)
          .get(`/users/me`)
          .matchHeader('authorization', `Bearer ${token}`)
          .reply(200, user);

        nockJophiel()
          .options(`/user-web/config`)
          .reply(200)
          .get(`/user-web/config`)
          .matchHeader('authorization', `Bearer ${token}`)
          .reply(200, config);

        await store.dispatch(loginActions.logIn(usernameOrEmail, password));
        expect(store.getActions()).toContainEqual(PutToken(token));
        expect(store.getActions()).toContainEqual(PutUser(user));
        expect(store.getActions()).toContainEqual(PutWebConfig(config));
      });
    });

    describe('when the credentials is invalid', () => {
      it('throws a more descriptive error', async () => {
        nockJophiel()
          .post(`/session/login`, { usernameOrEmail, password })
          .reply(403);

        await expect(store.dispatch(loginActions.logIn(usernameOrEmail, password))).rejects.toEqual(
          new Error('Invalid username/password.')
        );
      });
    });

    describe('when max concurrent sessions per user limit is exceeded', () => {
      it('throws a more descriptive error', async () => {
        nockJophiel()
          .post(`/session/login`, { usernameOrEmail, password })
          .reply(403, { errorName: 'Jophiel:UserMaxConcurrentSessionsExceeded' });

        await expect(store.dispatch(loginActions.logIn(usernameOrEmail, password))).rejects.toEqual(
          new Error('Login failed because you are trying to log in from too many places at once.')
        );
      });
    });
  });
});
