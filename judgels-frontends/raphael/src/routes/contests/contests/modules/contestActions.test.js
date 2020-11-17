import { push } from 'connected-react-router';
import nock from 'nock';
import { SubmissionError } from 'redux-form';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../conf';
import { ContestErrors, Contest, ContestStyle } from '../../../../modules/api/uriel/contest';
import * as contestActions from './contestActions';
import { EditContest, PutContest } from './contestReducer';

const contestJid = 'contest-jid';
const contest: Contest = {
  id: 1,
  jid: contestJid,
  slug: 'ioi',
  style: ContestStyle.IOI,
  name: 'IOI',
  beginTime: 100,
  duration: 5,
};
const mockStore = configureMockStore([thunk]);

describe('contestActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('createContest()', () => {
    const params = { slug: 'new-contest' };

    describe('when the slug does not already exist', () => {
      it('calls API to create contest', async () => {
        nock(APP_CONFIG.apiUrls.uriel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/contests`)
          .reply(200)
          .post(`/contests`, params)
          .reply(200);

        await store.dispatch(contestActions.createContest(params));

        expect(store.getActions()).toContainEqual(push('/contests/new-contest'));
        expect(store.getActions()).toContainEqual(EditContest.create(true));
      });
    });

    describe('when the slug already exists', () => {
      it('throws SubmissionError', async () => {
        nock(APP_CONFIG.apiUrls.uriel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/contests`)
          .reply(200)
          .post(`/contests`, params)
          .reply(400, { errorName: ContestErrors.SlugAlreadyExists });

        await expect(store.dispatch(contestActions.createContest(params))).rejects.toEqual(
          new SubmissionError({ slug: ContestErrors.SlugAlreadyExists })
        );
      });
    });
  });

  describe('updateContest()', () => {
    const slug = 'old-slug';

    describe('when the slug is not updated', () => {
      const params = { name: 'New Name' };

      it('calls API to update contest', async () => {
        nock(APP_CONFIG.apiUrls.uriel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/contests/${contestJid}`)
          .reply(200)
          .post(`/contests/${contestJid}`, params)
          .reply(200);

        await store.dispatch(contestActions.updateContest(contestJid, slug, params));
      });
    });

    describe('when the slug is updated', () => {
      const params = { slug: 'new-slug', name: 'New Name' };

      describe('when the slug does not already exist', () => {
        it('calls API to update contest', async () => {
          nock(APP_CONFIG.apiUrls.uriel)
            .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
            .options(`/contests/${contestJid}`)
            .reply(200)
            .post(`/contests/${contestJid}`, params)
            .reply(200);

          await store.dispatch(contestActions.updateContest(contestJid, slug, params));

          expect(store.getActions()).toContainEqual(push('/contests/new-slug'));
        });
      });

      describe('when the slug already exists', () => {
        it('throws SubmissionError', async () => {
          nock(APP_CONFIG.apiUrls.uriel)
            .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
            .options(`/contests/${contestJid}`)
            .reply(200)
            .post(`/contests/${contestJid}`, params)
            .reply(400, { errorName: ContestErrors.SlugAlreadyExists });

          await expect(store.dispatch(contestActions.updateContest(contestJid, slug, params))).rejects.toEqual(
            new SubmissionError({ slug: ContestErrors.SlugAlreadyExists })
          );
        });
      });
    });
  });

  describe('getContests()', () => {
    const name = 'contest-name';
    const page = 2;
    const responseBody = {
      totalCount: 3,
      page: [],
    };

    it('calls API to get contests', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests`)
        .query({ name, page })
        .reply(200, responseBody);

      const response = await store.dispatch(contestActions.getContests(name, page));
      expect(response).toEqual(responseBody);
    });
  });

  describe('getActiveContests()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API to get active contests', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/active`)
        .reply(200, responseBody);

      const response = await store.dispatch(contestActions.getActiveContests());
      expect(response).toEqual(responseBody);
    });
  });

  describe('getContestBySlug()', () => {
    it('calls API to get contest', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/slug/ioi`)
        .reply(200, contest);

      const response = await store.dispatch(contestActions.getContestBySlug('ioi'));
      expect(response).toEqual(contest);

      expect(store.getActions()).toContainEqual(PutContest.create(contest));
    });
  });

  describe('startVirtualContest()', () => {
    it('calls API to start virtual contest', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/contests/${contestJid}/virtual`)
        .reply(200)
        .post(`/contests/${contestJid}/virtual`)
        .reply(200);

      await store.dispatch(contestActions.startVirtualContest(contestJid));
    });
  });

  describe('resetVirtualContest()', () => {
    it('calls API to reset virtual contest', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/contests/${contestJid}/virtual/reset`)
        .reply(200)
        .put(`/contests/${contestJid}/virtual/reset`)
        .reply(200);

      await store.dispatch(contestActions.resetVirtualContest(contestJid));
    });
  });

  describe('getContestDescription()', () => {
    const description = 'This is a contest';

    it('calls API to get contest description', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/${contestJid}/description`)
        .reply(200, { description });

      const response = await store.dispatch(contestActions.getContestDescription(contestJid));
      expect(response).toEqual(description);
    });
  });

  describe('updateContestDescription()', () => {
    const description = 'This is a contest';

    it('calls API to update contest description', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/contests/${contestJid}/description`)
        .reply(200)
        .post(`/contests/${contestJid}/description`, { description })
        .reply(200, { description });

      await store.dispatch(contestActions.updateContestDescription(contestJid, description));
    });
  });
});
