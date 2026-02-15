import nock from 'nock';

import { nockUriel } from '../../../../../utils/nock';

import * as contestContestantActions from './contestContestantActions';

const contestJid = 'contestJid';

describe('contestContestantActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('getMyContestantState()', () => {
    const responseBody = { data: 'CONTESTANT' };

    it('calls API', async () => {
      nockUriel().get(`/contests/${contestJid}/contestants/me/state`).reply(200, responseBody);

      const response = await contestContestantActions.getMyContestantState(contestJid);
      expect(response).toEqual(responseBody);
    });
  });

  describe('getContestants()', () => {
    const page = 3;
    const responseBody = {
      data: {
        page: [{ jid: 'jid' }],
      },
    };

    it('calls API', async () => {
      nockUriel().get(`/contests/${contestJid}/contestants`).query({ page }).reply(200, responseBody);

      const response = await contestContestantActions.getContestants(contestJid, page);
      expect(response).toEqual(responseBody);
    });
  });

  describe('getApprovedContestantsCount()', () => {
    const responseBody = 3;

    it('calls API', async () => {
      nockUriel().get(`/contests/${contestJid}/contestants/approved/count`).reply(200, responseBody);

      const response = await contestContestantActions.getApprovedContestantsCount(contestJid);
      expect(response).toEqual(responseBody);
    });
  });

  describe('getApprovedContestants()', () => {
    const responseBody = {
      data: {
        page: [{ jid: 'jid' }],
      },
    };

    it('calls API', async () => {
      nockUriel().get(`/contests/${contestJid}/contestants/approved`).reply(200, responseBody);

      const response = await contestContestantActions.getApprovedContestants(contestJid);
      expect(response).toEqual(responseBody);
    });
  });

  describe('registerMyselfAsContestant()', () => {
    it('calls API', async () => {
      nockUriel().post(`/contests/${contestJid}/contestants/me`).reply(200);

      await contestContestantActions.registerMyselfAsContestant(contestJid);
    });
  });

  describe('unregisterMyselfAsContestant()', () => {
    it('calls API', async () => {
      nockUriel()
        .options(`/contests/${contestJid}/contestants/me`)
        .reply(200)
        .delete(`/contests/${contestJid}/contestants/me`)
        .reply(200);

      await contestContestantActions.unregisterMyselfAsContestant(contestJid);
    });
  });

  describe('upsertContestants()', () => {
    const usernames = ['username1'];
    const responseBody = {
      insertedContestantProfilesMap: {},
    };

    it('calls API', async () => {
      nockUriel().post(`/contests/${contestJid}/contestants/batch-upsert`, usernames).reply(200, responseBody);

      const response = await contestContestantActions.upsertContestants(contestJid, usernames);
      expect(response).toEqual(responseBody);
    });
  });

  describe('deleteContestants()', () => {
    const usernames = ['username1'];
    const responseBody = {
      deletedContestantProfilesMap: {},
    };

    it('calls API', async () => {
      nockUriel().post(`/contests/${contestJid}/contestants/batch-delete`, usernames).reply(200, responseBody);

      const response = await contestContestantActions.deleteContestants(contestJid, usernames);
      expect(response).toEqual(responseBody);
    });
  });
});
