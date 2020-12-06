import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockJophiel } from '../../../utils/nock';
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
    const info = { name: 'First Last' };

    it('calls API', async () => {
      nockJophiel()
        .get(`/users/${userJid}/info`)
        .reply(200, info);

      const response = await store.dispatch(infoActions.getInfo(userJid));
      expect(response).toEqual(info);
    });
  });

  describe('updateInfo()', () => {
    const info = { name: 'First Last' };
    const newInfo = { name: 'Last First' };

    it('calls API', async () => {
      nockJophiel()
        .options(`/users/${userJid}/info`)
        .reply(200)
        .put(`/users/${userJid}/info`, info)
        .reply(200, newInfo);

      await store.dispatch(infoActions.updateInfo(userJid, info));
    });
  });
});
