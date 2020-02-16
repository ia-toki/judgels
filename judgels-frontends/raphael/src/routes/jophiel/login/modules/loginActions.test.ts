import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { JophielRole } from '../../../../modules/api/jophiel/role';
import { User } from '../../../../modules/api/jophiel/user';
import { UserWebConfig } from '../../../../modules/api/jophiel/userWeb';
import { PutToken, PutUser } from '../../../../modules/session/sessionReducer';
import { APP_CONFIG } from '../../../../conf';
import * as loginActions from './loginActions';
import { PutWebConfig } from '../../modules/userWebReducer';

const path = 'path';
const usernameOrEmail = 'user';
const password = 'password';
const authCode = 'authCode';
const userJid = 'userJid';
const token = 'token123';
const user: User = { jid: userJid, username: usernameOrEmail, email: 'email' };
const config: UserWebConfig = { role: JophielRole.User };
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
        nock(APP_CONFIG.apiUrls.legacyJophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/session/login`)
          .reply(200)
          .post(`/session/login`, { usernameOrEmail, password })
          .reply(200, { authCode, token });

        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*', 'access-control-allow-headers': 'authorization' })
          .options(`/users/me`)
          .reply(200)
          .get(`/users/me`)
          .matchHeader('authorization', `Bearer ${token}`)
          .reply(200, user);

        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*', 'access-control-allow-headers': 'authorization' })
          .options(`/user-web/config`)
          .reply(200)
          .get(`/user-web/config`)
          .matchHeader('authorization', `Bearer ${token}`)
          .reply(200, config);

        await store.dispatch(loginActions.logIn(path, usernameOrEmail, password));
        expect(store.getActions()).toContainEqual(PutToken.create(token));
        expect(store.getActions()).toContainEqual(PutUser.create(user));
        expect(store.getActions()).toContainEqual(PutWebConfig.create(config));
      });
    });

    describe('when the credentials is invalid', () => {
      it('throws a more descriptive error', async () => {
        nock(APP_CONFIG.apiUrls.legacyJophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/session/login`)
          .reply(200)
          .post(`/session/login`, { usernameOrEmail, password })
          .reply(403);

        await expect(store.dispatch(loginActions.logIn(path, usernameOrEmail, password))).rejects.toEqual(
          new Error('Invalid username/password.')
        );
      });
    });
  });
});
