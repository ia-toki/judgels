import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../conf';
import { UserInfo } from '../../../modules/api/jophiel/userInfo';
import * as infoActions from './infoActions';

const userJid = 'user-jid';
const mockStore = configureMockStore([thunk]);

describe('infoActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getInfo()', () => {
    const info: UserInfo = { name: 'First Last' };

    it('calls API to get user info', async () => {
      nock(APP_CONFIG.apiUrls.jophiel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/users/${userJid}/info`)
        .reply(200, info);

      const response = await store.dispatch(infoActions.getInfo(userJid));
      expect(response).toEqual(info);
    });
  });

  describe('updateInfo()', () => {
    const info = { name: 'First Last' };
    const newInfo: UserInfo = { name: 'Last First' };

    it('calls API to update user info', async () => {
      nock(APP_CONFIG.apiUrls.jophiel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/users/${userJid}/info`)
        .reply(200)
        .put(`/users/${userJid}/info`, info)
        .reply(200, newInfo);

      await store.dispatch(infoActions.updateInfo(userJid, info));
    });
  });
});
