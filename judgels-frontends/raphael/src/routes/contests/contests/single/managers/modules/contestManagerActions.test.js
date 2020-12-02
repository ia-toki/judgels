import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockUriel } from '../../../../../../utils/nock';
import * as contestManagerActions from './contestManagerActions';

const contestJid = 'contestJid';
const mockStore = configureMockStore([thunk]);

describe('contestManagerActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getManagers()', () => {
    const page = 3;
    const responseBody = {
      data: {
        page: [{ jid: 'jid' }],
      },
    };

    it('calls API', async () => {
      nockUriel()
        .get(`/contests/${contestJid}/managers`)
        .query({ page })
        .reply(200, responseBody);

      const response = await store.dispatch(contestManagerActions.getManagers(contestJid, page));
      expect(response).toEqual(responseBody);
    });
  });

  describe('upsertManagers()', () => {
    const usernames = ['username1'];
    const responseBody = {
      insertedManagerProfilesMap: {},
    };

    it('calls API', async () => {
      nockUriel()
        .post(`/contests/${contestJid}/managers/batch-upsert`, usernames)
        .reply(200, responseBody);

      const response = await store.dispatch(contestManagerActions.upsertManagers(contestJid, usernames));
      expect(response).toEqual(responseBody);
    });
  });

  describe('deleteManagers()', () => {
    const usernames = ['username1'];
    const responseBody = {
      deletedManagerProfilesMap: {},
    };

    it('calls API', async () => {
      nockUriel()
        .post(`/contests/${contestJid}/managers/batch-delete`, usernames)
        .reply(200, responseBody);

      const response = await store.dispatch(contestManagerActions.deleteManagers(contestJid, usernames));
      expect(response).toEqual(responseBody);
    });
  });
});
