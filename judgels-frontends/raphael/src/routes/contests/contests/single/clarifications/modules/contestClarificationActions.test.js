import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockUriel } from '../../../../../../utils/nock';
import { ContestErrors } from '../../../../../../modules/api/uriel/contest';
import * as contestClarificationActions from './contestClarificationActions';

const contestJid = 'contestJid';
const clarificationJid = 'clarificationJid';
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

    it('calls API', async () => {
      nockUriel()
        .post(`/contests/${contestJid}/clarifications`, params)
        .reply(200);

      await store.dispatch(contestClarificationActions.createClarification(contestJid, params));
    });
  });

  describe('getClarifications()', () => {
    const status = undefined;
    const language = 'id';
    const page = 3;
    const responseBody = {
      data: {
        page: [{ jid: 'jid' }],
      },
    };

    it('calls API', async () => {
      nockUriel()
        .get(`/contests/${contestJid}/clarifications`)
        .query({ language, page })
        .reply(200, responseBody);

      const response = await store.dispatch(
        contestClarificationActions.getClarifications(contestJid, status, language, page)
      );
      expect(response).toEqual(responseBody);
    });
  });

  describe('answerClarification()', () => {
    const answer = 'Yes';

    describe('when the clarification has not been answered yet', () => {
      it('calls API', async () => {
        nockUriel()
          .options(`/contests/${contestJid}/clarifications/${clarificationJid}/answer`)
          .reply(200)
          .put(`/contests/${contestJid}/clarifications/${clarificationJid}/answer`, { answer })
          .reply(200);

        await store.dispatch(contestClarificationActions.answerClarification(contestJid, clarificationJid, answer));
      });
    });
    describe('when the clarification has already been answered', () => {
      it('throws Error', async () => {
        nockUriel()
          .options(`/contests/${contestJid}/clarifications/${clarificationJid}/answer`)
          .reply(200)
          .put(`/contests/${contestJid}/clarifications/${clarificationJid}/answer`, { answer })
          .reply(400, { errorName: ContestErrors.ClarificationAlreadyAnswered });

        await expect(
          store.dispatch(contestClarificationActions.answerClarification(contestJid, clarificationJid, answer))
        ).rejects.toEqual(new Error('This clarification has already been answered. Please refresh this page.'));
      });
    });
  });
});
