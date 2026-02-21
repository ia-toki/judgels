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
});
