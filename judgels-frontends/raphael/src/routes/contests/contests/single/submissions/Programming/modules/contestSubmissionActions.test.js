import { push } from 'connected-react-router';
import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../../../../conf';
import { NotFoundError } from '../../../../../../../modules/api/error';
import * as contestSubmissionActions from './contestSubmissionActions';

const contestJid = 'contest-jid';
const userJid = 'user-jid';
const username = 'username';
const problemJid = 'problem-jid';
const problemAlias = 'problem-alias';
const submissionId = 2;
const mockStore = configureMockStore([thunk]);

describe('contestSubmissionProgrammingActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getSubmissions()', () => {
    const page = 3;
    const responseBody = {
      data: [],
    };

    it('calls API to get programming submissions', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/submissions/programming`)
        .query({ contestJid, username, problemAlias, page })
        .reply(200, responseBody);

      const response = await store.dispatch(
        contestSubmissionActions.getSubmissions(contestJid, username, problemAlias, page)
      );
      expect(response).toEqual(responseBody);
    });
  });

  describe('getSubmissionWithSource()', () => {
    const language = 'id';

    describe('when the contestJid matches', () => {
      const responseBody = {
        data: {
          submission: {
            containerJid: contestJid,
          },
        },
      };

      it('calls API to get submission with source', async () => {
        nock(APP_CONFIG.apiUrls.uriel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .get(`/contests/submissions/programming/id/${submissionId}`)
          .query({ language })
          .reply(200, responseBody);

        const response = await store.dispatch(
          contestSubmissionActions.getSubmissionWithSource(contestJid, submissionId, language)
        );
        expect(response).toEqual(responseBody);
      });
    });

    describe('when the contestJid does not match', () => {
      const responseBody = {
        data: {
          submission: {
            containerJid: 'bogus',
          },
        },
      };

      it('throws not found error', async () => {
        nock(APP_CONFIG.apiUrls.uriel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .get(`/contests/submissions/programming/id/${submissionId}`)
          .query({ language })
          .reply(200, responseBody);

        await expect(
          store.dispatch(contestSubmissionActions.getSubmissionWithSource(contestJid, submissionId, language))
        ).rejects.toBeInstanceOf(NotFoundError);
      });
    });
  });

  describe('createSubmission()', () => {
    const sourceFiles = {
      encoder: { name: 'e' },
      decoder: { name: 'd' },
    };
    const gradingLanguage = 'Pascal';
    const data = {
      gradingLanguage,
      sourceFiles,
    };
    const contestSlug = 'contest-a';

    it('calls API to create a submission', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/contests/submissions/programming`)
        .reply(200)
        .post(`/contests/submissions/programming`)
        .reply(200);

      await store.dispatch(contestSubmissionActions.createSubmission(contestJid, contestSlug, problemJid, data));
      expect(store.getActions()).toContainEqual(push(`/contests/contest-a/submissions`));
    });
  });
});
