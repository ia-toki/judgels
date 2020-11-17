import nock from 'nock';
import { SubmissionError } from 'redux-form';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../conf';
import { UserRegistrationData } from '../../../../modules/api/jophiel/userAccount';
import * as registerActions from './registerActions';

const mockStore = configureMockStore([thunk]);

describe('registerActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('registerUser()', () => {
    const data: UserRegistrationData = {
      username: 'user',
      name: 'name',
      email: 'email@domain.com',
      password: 'pass',
    };

    describe('when username already exists', () => {
      it('throws SubmissionError', async () => {
        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .get(`/user-search/username-exists/${data.username}`)
          .reply(200, 'true');

        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .get(`/user-search/email-exists/${data.email}`)
          .reply(200, 'false');

        await expect(store.dispatch(registerActions.registerUser(data))).rejects.toBeInstanceOf(SubmissionError);
      });
    });

    describe('when email already exists', () => {
      it('throws SubmissionError', async () => {
        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .get(`/user-search/username-exists/${data.username}`)
          .reply(200, 'false');

        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .get(`/user-search/email-exists/${data.email}`)
          .reply(200, 'true');

        await expect(store.dispatch(registerActions.registerUser(data))).rejects.toBeInstanceOf(SubmissionError);
      });
    });

    describe('when the form is valid', () => {
      it('tries to register user', async () => {
        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .get(`/user-search/username-exists/${data.username}`)
          .reply(200, 'false');

        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .get(`/user-search/email-exists/${data.email}`)
          .reply(200, 'false');

        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/user-account/register`)
          .reply(200)
          .post(`/user-account/register`, data as any)
          .reply(200);

        await store.dispatch(registerActions.registerUser(data));
      });
    });
  });
});
