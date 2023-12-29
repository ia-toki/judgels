import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockJophiel } from '../../../utils/nock';

import * as avatarActions from './avatarActions';

const userJid = 'user-jid';
const mockStore = configureMockStore([thunk]);

describe('avatarActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('updateAvatar()', () => {
    const file = {};

    it('calls API', async () => {
      nockJophiel().post(`/users/${userJid}/avatar`).reply(200);

      await store.dispatch(avatarActions.updateAvatar(userJid, file));
    });
  });

  describe('deleteAvatar()', () => {
    it('calls API', async () => {
      nockJophiel().options(`/users/${userJid}/avatar`).reply(200).delete(`/users/${userJid}/avatar`).reply(200);

      await store.dispatch(avatarActions.deleteAvatar(userJid));
    });
  });
});
