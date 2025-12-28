import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { NotFoundError } from '../../../../../../../modules/api/error';
import { nockUriel } from '../../../../../../../utils/nock';

import * as contestSubmissionActions from './contestSubmissionActions';

const mockPush = vi.fn();
const mockReplace = vi.fn();

vi.mock('../../../../../../../modules/navigation/navigationRef', () => ({
  getNavigationRef: () => ({
    push: mockPush,
    replace: mockReplace,
  }),
}));

const contestJid = 'contest-jid';
const username = 'username';
const problemJid = 'problem-jid';
const problemAlias = 'problem-alias';
const submissionId = 2;
const mockStore = configureMockStore([thunk]);

describe('contestSubmissionProgrammingActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
    mockPush.mockClear();
    mockReplace.mockClear();
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('getSubmissions()', () => {
    const page = 3;
    const responseBody = {
      data: [],
    };

    it('calls API', async () => {
      nockUriel()
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

      it('calls API', async () => {
        nockUriel()
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
        nockUriel()
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

    it('calls API', async () => {
      nockUriel().post(`/contests/submissions/programming`).reply(200);

      await store.dispatch(contestSubmissionActions.createSubmission(contestJid, contestSlug, problemJid, data));
      expect(mockPush).toHaveBeenCalledWith(`/contests/contest-a/submissions`);
    });
  });
});
