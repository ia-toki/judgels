import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../conf';
import { NotFoundError } from '../../../modules/api/error';
import * as profileActions from './profileActions';
import { PutUser } from './profileReducer';

const userJid = 'user-jid';
const username = 'username';
const mockStore = configureMockStore([thunk]);

describe('profileActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getUser()', () => {
    describe('when user found', () => {
      it('calls API to get user', async () => {
        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/user-search/username-to-jid`)
          .reply(200)
          .post(`/user-search/username-to-jid`)
          .reply(200, { username: userJid });

        await store.dispatch(profileActions.getUser(username));
        expect(store.getActions()).toContainEqual(PutUser.create({ userJid, username }));
      });
    });

    describe('when user not found', () => {
      it('throws NotFoundError', async () => {
        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/user-search/username-to-jid`)
          .reply(200)
          .post(`/user-search/username-to-jid`)
          .reply(200, {});

        await expect(store.dispatch(profileActions.getUser(username))).rejects.toBeInstanceOf(NotFoundError);
      });
    });
  });
});
