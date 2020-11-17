import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../../../../conf';
import * as contestSubmissionActions from './contestSubmissionActions';

const contestJid = 'contest-jid';
const problemJid = 'problem-jid';
const mockStore = configureMockStore([thunk]);

describe('contestSubmissionBundleActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getSubmissions()', () => {
    const username = 'username';
    const problemAlias = 'alias';
    const page = 3;
    const responseBody = {
      data: [],
    };

    it('calls API to get bundle submissions', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/submissions/bundle`)
        .query({ contestJid, username, problemAlias, page })
        .reply(200, responseBody);

      const response = await store.dispatch(
        contestSubmissionActions.getSubmissions(contestJid, username, problemAlias, page)
      );
      expect(response).toEqual(responseBody);
    });
  });

  describe('createItemSubmission()', () => {
    const itemJid = 'item-jid';
    const answer = 'answer';

    it('calls API to create submission', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/contests/submissions/bundle`)
        .reply(200)
        .post(`/contests/submissions/bundle`, { containerJid: contestJid, problemJid, itemJid, answer })
        .reply(200);

      await store.dispatch(contestSubmissionActions.createItemSubmission(contestJid, problemJid, itemJid, answer));
    });
  });

  describe('getSubmissionSummary()', () => {
    const username = 'username';
    const language = 'id';
    const responseBody = {
      itemJidsByProblemJid: {},
    };

    it('calls API to get summary', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/submissions/bundle/summary`)
        .query({ contestJid, username, language })
        .reply(200, responseBody);

      const response = await store.dispatch(
        contestSubmissionActions.getSubmissionSummary(contestJid, username, language)
      );
      expect(response).toEqual(responseBody);
    });
  });

  describe('getLatestSubmissions()', () => {
    const problemAlias = 'alias';
    const responseBody = {
      id: {},
    };

    it('calls API to get latest submissions', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/submissions/bundle/answers`)
        .query({ contestJid, problemAlias })
        .reply(200, responseBody);

      const response = await store.dispatch(contestSubmissionActions.getLatestSubmissions(contestJid, problemAlias));
      expect(response).toEqual(responseBody);
    });
  });
});
