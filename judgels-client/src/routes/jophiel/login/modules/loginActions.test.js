import nock from 'nock';

import { queryClient } from '../../../../modules/queryClient';
import { getToken, getUser } from '../../../../modules/session';
import { nockJophiel } from '../../../../utils/nock';

import * as loginActions from './loginActions';

const usernameOrEmail = 'user';
const password = 'password';
const authCode = 'authCode';
const userJid = 'userJid';
const token = 'token123';
const user = { jid: userJid, username: usernameOrEmail, email: 'email' };
const config = { role: {} };

describe('loginActions', () => {
  afterEach(function () {
    nock.cleanAll();
    queryClient.clear();
  });

  describe('logIn()', () => {
    describe('when the credentials is valid', () => {
      it('succeeds', async () => {
        nockJophiel().post(`/session/login`, { usernameOrEmail, password }).reply(200, { authCode, token });

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

        await loginActions.logIn(usernameOrEmail, password);
        expect(getToken()).toBe(token);
        expect(getUser()).toEqual(user);
        expect(queryClient.getQueryData(['user-web-config'])).toEqual(config);
      });
    });

    describe('when the credentials is invalid', () => {
      it('throws a more descriptive error', async () => {
        nockJophiel().post(`/session/login`, { usernameOrEmail, password }).reply(403);

        await expect(loginActions.logIn(usernameOrEmail, password)).rejects.toEqual(
          new Error('Invalid username/password.')
        );
      });
    });

    describe('when max concurrent sessions per user limit is exceeded', () => {
      it('throws a more descriptive error', async () => {
        nockJophiel()
          .post(`/session/login`, { usernameOrEmail, password })
          .reply(403, { message: 'Jophiel:UserMaxConcurrentSessionsExceeded' });

        await expect(loginActions.logIn(usernameOrEmail, password)).rejects.toEqual(
          new Error('Login failed because you are trying to log in from too many places at once.')
        );
      });
    });
  });
});
