import nock from 'nock';

import { nockUriel } from '../../../../../../../utils/nock';

import * as contestSubmissionActions from './contestSubmissionActions';

const contestJid = 'contest-jid';
const problemJid = 'problem-jid';

describe('contestSubmissionBundleActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('getSubmissions()', () => {
    const username = 'username';
    const problemAlias = 'alias';
    const page = 3;
    const responseBody = {
      data: [],
    };

    it('calls API', async () => {
      nockUriel()
        .get(`/contests/submissions/bundle`)
        .query({ contestJid, username, problemAlias, page })
        .reply(200, responseBody);

      const response = await contestSubmissionActions.getSubmissions(contestJid, username, problemAlias, page);
      expect(response).toEqual(responseBody);
    });
  });

  describe('createItemSubmission()', () => {
    const itemJid = 'item-jid';
    const answer = 'answer';

    it('calls API to create submission', async () => {
      nockUriel()
        .post(`/contests/submissions/bundle`, { containerJid: contestJid, problemJid, itemJid, answer })
        .reply(200);

      await contestSubmissionActions.createItemSubmission(contestJid, problemJid, itemJid, answer);
    });
  });

  describe('getSubmissionSummary()', () => {
    const username = 'username';
    const language = 'id';
    const responseBody = {
      itemJidsByProblemJid: {},
    };

    it('calls API ', async () => {
      nockUriel()
        .get(`/contests/submissions/bundle/summary`)
        .query({ contestJid, username, language })
        .reply(200, responseBody);

      const response = await contestSubmissionActions.getSubmissionSummary(contestJid, username, language);
      expect(response).toEqual(responseBody);
    });
  });

  describe('getLatestSubmissions()', () => {
    const problemAlias = 'alias';
    const responseBody = {
      id: {},
    };

    it('calls API', async () => {
      nockUriel()
        .get(`/contests/submissions/bundle/answers`)
        .query({ contestJid, problemAlias })
        .reply(200, responseBody);

      const response = await contestSubmissionActions.getLatestSubmissions(contestJid, problemAlias);
      expect(response).toEqual(responseBody);
    });
  });
});
