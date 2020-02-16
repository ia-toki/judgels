import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../conf';
import * as avatarActions from './avatarActions';

const userJid = 'user-jid';
const mockStore = configureMockStore([thunk]);

describe('avatarActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('updateAvatar()', () => {
    const file = {} as File;

    it('calls API to update avatar', async () => {
      nock(APP_CONFIG.apiUrls.jophiel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/users/${userJid}/avatar`)
        .reply(200)
        .post(`/users/${userJid}/avatar`)
        .reply(200);

      await store.dispatch(avatarActions.updateAvatar(userJid, file));
    });
  });

  describe('deleteAvatar()', () => {
    it('calls API to delete avatar', async () => {
      nock(APP_CONFIG.apiUrls.jophiel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/users/${userJid}/avatar`)
        .reply(200)
        .delete(`/users/${userJid}/avatar`)
        .reply(200);

      await store.dispatch(avatarActions.deleteAvatar(userJid));
    });
  });
});
