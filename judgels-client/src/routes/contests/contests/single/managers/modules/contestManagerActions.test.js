import nock from 'nock';

import { nockUriel } from '../../../../../../utils/nock';

import * as contestManagerActions from './contestManagerActions';

const contestJid = 'contestJid';

describe('contestManagerActions', () => {
  afterEach(function () {
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
      nockUriel().get(`/contests/${contestJid}/managers`).query({ page }).reply(200, responseBody);

      const response = await contestManagerActions.getManagers(contestJid, page);
      expect(response).toEqual(responseBody);
    });
  });

  describe('upsertManagers()', () => {
    const usernames = ['username1'];
    const responseBody = {
      insertedManagerProfilesMap: {},
    };

    it('calls API', async () => {
      nockUriel().post(`/contests/${contestJid}/managers/batch-upsert`, usernames).reply(200, responseBody);

      const response = await contestManagerActions.upsertManagers(contestJid, usernames);
      expect(response).toEqual(responseBody);
    });
  });

  describe('deleteManagers()', () => {
    const usernames = ['username1'];
    const responseBody = {
      deletedManagerProfilesMap: {},
    };

    it('calls API', async () => {
      nockUriel().post(`/contests/${contestJid}/managers/batch-delete`, usernames).reply(200, responseBody);

      const response = await contestManagerActions.deleteManagers(contestJid, usernames);
      expect(response).toEqual(responseBody);
    });
  });
});
