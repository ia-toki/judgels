import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { ContestErrors } from '../../../../modules/api/uriel/contest';
import { SubmissionError } from '../../../../modules/form/submissionError';
import { nockUriel } from '../../../../utils/nock';

import * as contestActions from './contestActions';

const mockPush = vi.fn();
const mockReplace = vi.fn();

vi.mock('../../../../modules/navigation/navigationRef', () => ({
  getNavigationRef: () => ({
    push: mockPush,
    replace: mockReplace,
  }),
}));

const contestJid = 'contestJid';
const mockStore = configureMockStore([thunk]);

describe('contestActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
    mockPush.mockClear();
    mockReplace.mockClear();
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('createContest()', () => {
    const params = { slug: 'new-contest' };

    describe('when the slug does not already exist', () => {
      it('calls API', async () => {
        nockUriel().post(`/contests`, params).reply(200);

        await store.dispatch(contestActions.createContest(params));

        expect(mockPush).toHaveBeenCalledWith('/contests/new-contest', { isEditingContest: true });
      });
    });

    describe('when the slug already exists', () => {
      it('throws SubmissionError', async () => {
        nockUriel().post(`/contests`, params).reply(400, { message: ContestErrors.SlugAlreadyExists });

        await expect(store.dispatch(contestActions.createContest(params))).rejects.toEqual(
          new SubmissionError({ slug: 'Slug already exists' })
        );
      });
    });
  });

  describe('updateContest()', () => {
    const slug = 'old-slug';

    describe('when the slug is not updated', () => {
      const params = { name: 'New Name' };

      it('calls API', async () => {
        nockUriel().post(`/contests/${contestJid}`, params).reply(200);

        await store.dispatch(contestActions.updateContest(contestJid, slug, params));
      });
    });

    describe('when the slug is updated', () => {
      const params = { slug: 'new-slug', name: 'New Name' };

      describe('when the slug does not already exist', () => {
        it('calls API', async () => {
          nockUriel().post(`/contests/${contestJid}`, params).reply(200);

          await store.dispatch(contestActions.updateContest(contestJid, slug, params));

          expect(mockPush).toHaveBeenCalledWith('/contests/new-slug');
        });
      });

      describe('when the slug already exists', () => {
        it('throws SubmissionError', async () => {
          nockUriel().post(`/contests/${contestJid}`, params).reply(400, { message: ContestErrors.SlugAlreadyExists });

          await expect(store.dispatch(contestActions.updateContest(contestJid, slug, params))).rejects.toEqual(
            new SubmissionError({ slug: 'Slug already exists' })
          );
        });
      });
    });
  });

  describe('getContests()', () => {
    const name = 'contest-name';
    const page = 2;
    const responseBody = {
      data: {
        page: [{ id: 1 }],
      },
    };

    it('calls API', async () => {
      nockUriel().get(`/contests`).query({ name, page }).reply(200, responseBody);

      const response = await store.dispatch(contestActions.getContests(name, page));
      expect(response).toEqual(responseBody);
    });
  });

  describe('getActiveContests()', () => {
    const responseBody = {
      data: [{ id: 1 }],
    };

    it('calls API', async () => {
      nockUriel().get(`/contests/active`).reply(200, responseBody);

      const response = await store.dispatch(contestActions.getActiveContests());
      expect(response).toEqual(responseBody);
    });
  });

  describe('getContestBySlug()', () => {
    const contest = { id: 1 };

    it('calls API', async () => {
      nockUriel().get(`/contests/slug/ioi`).reply(200, contest);

      const response = await store.dispatch(contestActions.getContestBySlug('ioi'));
      expect(response).toEqual(contest);
    });
  });

  describe('startVirtualContest()', () => {
    it('calls API', async () => {
      nockUriel().post(`/contests/${contestJid}/virtual`).reply(200);

      await store.dispatch(contestActions.startVirtualContest(contestJid));
    });
  });

  describe('resetVirtualContest()', () => {
    it('calls API', async () => {
      nockUriel()
        .options(`/contests/${contestJid}/virtual/reset`)
        .reply(200)
        .put(`/contests/${contestJid}/virtual/reset`)
        .reply(200);

      await store.dispatch(contestActions.resetVirtualContest(contestJid));
    });
  });

  describe('getContestDescription()', () => {
    const description = 'This is a contest';

    it('calls API', async () => {
      nockUriel().get(`/contests/${contestJid}/description`).reply(200, { description });

      const response = await store.dispatch(contestActions.getContestDescription(contestJid));
      expect(response).toEqual({ description });
    });
  });

  describe('updateContestDescription()', () => {
    const description = 'This is a contest';

    it('calls API', async () => {
      nockUriel().post(`/contests/${contestJid}/description`, { description }).reply(200, { description });

      await store.dispatch(contestActions.updateContestDescription(contestJid, description));
    });
  });
});
