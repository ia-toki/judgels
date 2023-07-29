import { push } from 'connected-react-router';
import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { SubmissionError } from '../../../modules/form/submissionError';
import { PutWebConfig } from '../modules/userWebReducer';
import { PutToken, PutUser } from '../../../modules/session/sessionReducer';
import { nockJophiel } from '../../../utils/nock';
import * as googleAuthActions from './googleAuthActions';

const authCode = 'authCode';
const userJid = 'userJid';
const token = 'token123';
const idToken = 'google-id-token';
const username = 'user';
const user = { jid: userJid, username, email: 'email' };
const config = { role: {} };
const mockStore = configureMockStore([thunk]);

describe('googleAuthActions', () => {
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
          .post(`/session/login-google`, { idToken })
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

        await store.dispatch(googleAuthActions.logIn({ tokenId: idToken }));
        expect(store.getActions()).toContainEqual(PutToken(token));
        expect(store.getActions()).toContainEqual(PutUser(user));
        expect(store.getActions()).toContainEqual(PutWebConfig(config));
      });
    });

    describe('when the credentials is invalid', () => {
      it('returns false', async () => {
        nockJophiel()
          .post(`/session/login-google`, { idToken })
          .reply(403);

        const isLoggedIn = await store.dispatch(googleAuthActions.logIn({ tokenId: idToken }));
        expect(isLoggedIn).toBeFalsy();
      });
    });
  });

  describe('register()', () => {
    describe('when username is valid', () => {
      it('succeeds', async () => {
        nockJophiel()
          .get(`/user-search/username-exists/${username}`)
          .reply(200, 'false');

        nockJophiel()
          .post(`/user-account/register-google`, { idToken, username })
          .reply(200);

        nockJophiel()
          .post(`/session/login-google`, { idToken })
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

        await store.dispatch(googleAuthActions.register({ idToken, username }));
        expect(store.getActions()).toContainEqual(push('/registered?source=google'));
        expect(store.getActions()).toContainEqual(PutToken(token));
        expect(store.getActions()).toContainEqual(PutUser(user));
        expect(store.getActions()).toContainEqual(PutWebConfig(config));
      });
    });

    describe('when username already exists', () => {
      it('throws SubmissionError', async () => {
        nockJophiel()
          .get(`/user-search/username-exists/${username}`)
          .reply(200, 'true');

        await expect(store.dispatch(googleAuthActions.register({ idToken, username }))).rejects.toEqual(
          new SubmissionError({ username: 'Username already exists' })
        );
      });
    });
  });
});
