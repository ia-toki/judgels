import nock from 'nock';

import { nockUriel } from '../../../../../../utils/nock';

import * as contestScoreboardActions from './contestScoreboardActions';

const contestJid = 'contest-jid';

describe('contestScoreboardActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('getScoreboard()', () => {
    const frozen = true;
    const showClosedProblems = false;
    const page = 1;
    const responseBody = {
      data: {},
    };

    it('calls API', async () => {
      nockUriel()
        .get(`/contests/${contestJid}/scoreboard`)
        .query({ frozen, showClosedProblems, page })
        .reply(200, responseBody);

      const response = await contestScoreboardActions.getScoreboard(contestJid, frozen, showClosedProblems, page);
      expect(response).toEqual(responseBody);
    });
  });

  describe('refreshScoreboard()', () => {
    it('calls API', async () => {
      nockUriel().post(`/contests/${contestJid}/scoreboard/refresh`).reply(200);

      await contestScoreboardActions.refreshScoreboard(contestJid);
    });
  });

  describe('getSubmissionInfo()', () => {
    const userJid = 'user-jid';
    const problemJid = 'problem-jid';
    const responseBody = {
      data: {},
    };

    it('calls API', async () => {
      nockUriel()
        .get(`/contests/submissions/programming/info`)
        .query({ contestJid, userJid, problemJid })
        .reply(200, responseBody);

      const response = await contestScoreboardActions.getSubmissionInfo(contestJid, userJid, problemJid);
      expect(response).toEqual(responseBody);
    });
  });
});
