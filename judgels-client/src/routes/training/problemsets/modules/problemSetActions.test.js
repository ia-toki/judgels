import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../conf';
import { ProblemSetErrors } from '../../../../modules/api/jerahmeel/problemSet';
import { SubmissionError } from '../../../../modules/form/submissionError';

import * as problemSetActions from './problemSetActions';

const problemSetJid = 'problemSet-jid';
const mockStore = configureMockStore([thunk]);

describe('problemSetActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('createProblemSet()', () => {
    const params = { slug: 'new-problem-set', name: 'New Problemset', archiveSlug: 'archive' };

    describe('when the slug does not already exist', () => {
      it('calls API to create problemset', async () => {
        nock(APP_CONFIG.apiUrl)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/problemsets`)
          .reply(200)
          .post(`/problemsets`, params)
          .reply(200);

        await store.dispatch(problemSetActions.createProblemSet(params));
      });
    });

    describe('when the slug already exists', () => {
      it('throws SubmissionError', async () => {
        nock(APP_CONFIG.apiUrl)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/problemsets`)
          .reply(200)
          .post(`/problemsets`, params)
          .reply(400, { message: ProblemSetErrors.SlugAlreadyExists });

        await expect(store.dispatch(problemSetActions.createProblemSet(params))).rejects.toEqual(
          new SubmissionError({ slug: 'Slug already exists' })
        );
      });
    });

    describe('when archive slug not found', () => {
      it('throws SubmissionError', async () => {
        nock(APP_CONFIG.apiUrl)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/problemsets`)
          .reply(200)
          .post(`/problemsets`, params)
          .reply(400, { message: ProblemSetErrors.ArchiveSlugNotFound });

        await expect(store.dispatch(problemSetActions.createProblemSet(params))).rejects.toEqual(
          new SubmissionError({ archiveSlug: 'Archive slug not found' })
        );
      });
    });
  });

  describe('updateProblemSet()', () => {
    describe('when the slug is not updated', () => {
      const params = { name: 'New Name' };

      it('calls API to update problemset', async () => {
        nock(APP_CONFIG.apiUrl)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/problemsets/${problemSetJid}`)
          .reply(200)
          .post(`/problemsets/${problemSetJid}`, params)
          .reply(200);

        await store.dispatch(problemSetActions.updateProblemSet(problemSetJid, params));
      });
    });

    describe('when the slug is updated', () => {
      const params = { slug: 'new-slug', name: 'New Name' };

      describe('when the slug does not already exist', () => {
        it('calls API to update problemset', async () => {
          nock(APP_CONFIG.apiUrl)
            .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
            .options(`/problemsets/${problemSetJid}`)
            .reply(200)
            .post(`/problemsets/${problemSetJid}`, params)
            .reply(200);

          await store.dispatch(problemSetActions.updateProblemSet(problemSetJid, params));
        });
      });

      describe('when the slug already exists', () => {
        it('throws SubmissionError', async () => {
          nock(APP_CONFIG.apiUrl)
            .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
            .options(`/problemsets/${problemSetJid}`)
            .reply(200)
            .post(`/problemsets/${problemSetJid}`, params)
            .reply(400, { message: ProblemSetErrors.SlugAlreadyExists });

          await expect(store.dispatch(problemSetActions.updateProblemSet(problemSetJid, params))).rejects.toEqual(
            new SubmissionError({ slug: 'Slug already exists' })
          );
        });
      });
    });
    describe('when archive slug is updated', () => {
      describe('when archive slug not found', () => {
        const params = { archiveSlug: 'new-archive' };

        it('throws SubmissionError', async () => {
          nock(APP_CONFIG.apiUrl)
            .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
            .options(`/problemsets`)
            .reply(200)
            .post(`/problemsets/${problemSetJid}`, params)
            .reply(400, { message: ProblemSetErrors.ArchiveSlugNotFound });

          await expect(store.dispatch(problemSetActions.updateProblemSet(problemSetJid, params))).rejects.toEqual(
            new SubmissionError({ archiveSlug: 'Archive slug not found' })
          );
        });
      });
    });
  });

  describe('getProblemSets()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API to get problemsets', async () => {
      nock(APP_CONFIG.apiUrl)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/problemsets`)
        .query({ page: 1 })
        .reply(200, responseBody);

      const response = await store.dispatch(problemSetActions.getProblemSets(1));
      expect(response).toEqual(responseBody);
    });
  });

  describe('getProblems()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API to get problems', async () => {
      nock(APP_CONFIG.apiUrl)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/problemsets/${problemSetJid}/problems`)
        .reply(200, responseBody);

      const response = await store.dispatch(problemSetActions.getProblems(problemSetJid));
      expect(response).toEqual(responseBody);
    });
  });

  describe('setProblems()', () => {
    const params = [];

    describe('when success', () => {
      it('calls API to set problems', async () => {
        nock(APP_CONFIG.apiUrl)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/problemsets/${problemSetJid}/problems`)
          .reply(200)
          .put(`/problemsets/${problemSetJid}/problems`, params)
          .reply(200);

        await store.dispatch(problemSetActions.setProblems(problemSetJid, params));
      });
    });

    describe('when some contests are not found', () => {
      it('throws SubmissionError', async () => {
        nock(APP_CONFIG.apiUrl)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/problemsets/${problemSetJid}/problems`)
          .reply(200)
          .put(`/problemsets/${problemSetJid}/problems`, params)
          .reply(403, {
            message: ProblemSetErrors.ContestSlugsNotAllowed,
            args: { contestSlugs: ['foo', 'bar'] },
          });

        await expect(store.dispatch(problemSetActions.setProblems(problemSetJid, params))).rejects.toEqual(
          new SubmissionError({ problems: 'Contests not found/allowed: foo,bar' })
        );
      });
    });
  });
});
