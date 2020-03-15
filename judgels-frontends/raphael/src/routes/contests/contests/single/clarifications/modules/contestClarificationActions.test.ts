import nock from 'nock';
import { SubmissionError } from 'redux-form';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../../../conf';
import { ContestErrors } from '../../../../../../modules/api/uriel/contest';
import * as contestClarificationActions from './contestClarificationActions';

const contestJid = 'contest-jid';
const clarificationJid = 'clarification-jid';
const mockStore = configureMockStore([thunk]);

describe('contestClarificationActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('createClarification()', () => {
    const params = {
      title: 'Clarification',
      topicJid: 'topic-jid',
      question: 'Question',
    };

    it('calls API to create clarification', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/contests/${contestJid}/clarifications`)
        .reply(200)
        .post(`/contests/${contestJid}/clarifications`, params)
        .reply(200);

      await store.dispatch(contestClarificationActions.createClarification(contestJid, params));
    });
  });

  describe('getClarifications()', () => {
    const language = 'id';
    const page = 3;
    const responseBody = {
      data: [],
    };

    it('calls API to get clarifications', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/${contestJid}/clarifications`)
        .query({ language, page })
        .reply(200, responseBody);

      const response = await store.dispatch(contestClarificationActions.getClarifications(contestJid, language, page));
      expect(response).toEqual(responseBody);
    });
  });

  describe('answerClarification()', () => {
    const answer = 'Yes';

    describe('when the clarification has not been answered yet', () => {
      it('calls API to answer clarification', async () => {
        nock(APP_CONFIG.apiUrls.uriel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/contests/${contestJid}/clarifications/${clarificationJid}/answer`)
          .reply(200)
          .put(`/contests/${contestJid}/clarifications/${clarificationJid}/answer`, { answer })
          .reply(200);

        await store.dispatch(contestClarificationActions.answerClarification(contestJid, clarificationJid, answer));
      });
    });
    describe('when the clarification has already been answered', () => {
      it('calls API to answer clarification', async () => {
        nock(APP_CONFIG.apiUrls.uriel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/contests/${contestJid}/clarifications/${clarificationJid}/answer`)
          .reply(200)
          .put(`/contests/${contestJid}/clarifications/${clarificationJid}/answer`, { answer })
          .reply(400, { errorName: ContestErrors.ClarificationAlreadyAnswered });

        await expect(
          store.dispatch(contestClarificationActions.answerClarification(contestJid, clarificationJid, answer))
        ).rejects.toEqual(
          new SubmissionError({ _error: 'This clarification has already been answered. Please refresh this page.' })
        );
      });
    });
  });
});
