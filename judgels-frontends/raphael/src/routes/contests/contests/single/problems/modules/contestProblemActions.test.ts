import nock from 'nock';
import { SubmissionError } from 'redux-form';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../../../conf';
import { ContestErrors } from '../../../../../../modules/api/uriel/contest';
import { ContestProblemData } from '../../../../../../modules/api/uriel/contestProblem';
import * as contestProblemActions from './contestProblemActions';

const contestJid = 'contest-jid';
const mockStore = configureMockStore([thunk]);

describe('contestProblemActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getProblems()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API to get problems', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/${contestJid}/problems`)
        .reply(200, responseBody);

      const response = await store.dispatch(contestProblemActions.getProblems(contestJid));
      expect(response).toEqual(responseBody);
    });
  });

  describe('setProblems()', () => {
    const data: ContestProblemData[] = [
      { slug: 'slug1' } as ContestProblemData,
      { slug: 'slug2' } as ContestProblemData,
      { slug: 'slug3' } as ContestProblemData,
      { slug: 'slug4' } as ContestProblemData,
    ];

    describe('when all slugs are valid', () => {
      it('calls API to set problems', async () => {
        nock(APP_CONFIG.apiUrls.uriel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/contests/${contestJid}/problems`)
          .reply(200)
          .put(`/contests/${contestJid}/problems`, data as any)
          .reply(200);

        await store.dispatch(contestProblemActions.setProblems(contestJid, data));
      });
    });

    describe('when not all slugs are valid', () => {
      it('throws SubmissionError', async () => {
        nock(APP_CONFIG.apiUrls.uriel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/contests/${contestJid}/problems`)
          .reply(200)
          .put(`/contests/${contestJid}/problems`, data as any)
          .reply(403, {
            errorName: ContestErrors.ProblemSlugsNotAllowed,
            parameters: { slugs: 'slug2, slug4' },
          });

        await expect(store.dispatch(contestProblemActions.setProblems(contestJid, data))).rejects.toEqual(
          new SubmissionError({ problems: 'Problems not found/allowed: slug2, slug4' })
        );
      });
    });
  });

  describe('getBundleProblemWorksheet()', () => {
    const problemAlias = 'C';
    const language = 'id';
    const responseBody = {
      worksheet: {},
    };

    it('calls API to get bundle problem worksheet', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/${contestJid}/problems/${problemAlias}/bundle/worksheet`)
        .query({ language })
        .reply(200, responseBody);

      const response = await store.dispatch(
        contestProblemActions.getBundleProblemWorksheet(contestJid, problemAlias, language)
      );
      expect(response).toEqual(responseBody);
    });
  });

  describe('getProgrammingProblemWorksheet()', () => {
    const problemAlias = 'C';
    const language = 'id';
    const responseBody = {
      worksheet: {},
    };

    it('calls API to get programming problem worksheet', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/${contestJid}/problems/${problemAlias}/programming/worksheet`)
        .query({ language })
        .reply(200, responseBody);

      const response = await store.dispatch(
        contestProblemActions.getProgrammingProblemWorksheet(contestJid, problemAlias, language)
      );
      expect(response).toEqual(responseBody);
    });
  });
});
