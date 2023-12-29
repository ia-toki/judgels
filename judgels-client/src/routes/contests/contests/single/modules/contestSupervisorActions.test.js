import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockUriel } from '../../../../../utils/nock';

import * as contestSupervisorActions from './contestSupervisorActions';

const contestJid = 'contestJid';
const mockStore = configureMockStore([thunk]);

describe('contestSupervisorActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('getSupervisors()', () => {
    const page = 3;
    const responseBody = {
      data: {
        page: [{ jid: 'jid' }],
      },
    };

    it('calls API', async () => {
      nockUriel().get(`/contests/${contestJid}/supervisors`).query({ page }).reply(200, responseBody);

      const response = await store.dispatch(contestSupervisorActions.getSupervisors(contestJid, page));
      expect(response).toEqual(responseBody);
    });
  });

  describe('upsertSupervisors()', () => {
    const data = {
      usernames: ['username1'],
    };
    const responseBody = {
      upsertedSupervisorProfilesMap: {},
    };

    it('calls API', async () => {
      nockUriel().post(`/contests/${contestJid}/supervisors/batch-upsert`, data).reply(200, responseBody);

      const response = await store.dispatch(contestSupervisorActions.upsertSupervisors(contestJid, data));
      expect(response).toEqual(responseBody);
    });
  });

  describe('deleteSupervisors()', () => {
    const usernames = ['username1'];
    const responseBody = {
      deletedSupervisorProfilesMap: {},
    };

    it('calls API', async () => {
      nockUriel().post(`/contests/${contestJid}/supervisors/batch-delete`, usernames).reply(200, responseBody);

      const response = await store.dispatch(contestSupervisorActions.deleteSupervisors(contestJid, usernames));
      expect(response).toEqual(responseBody);
    });
  });
});
