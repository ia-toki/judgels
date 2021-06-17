import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockUriel } from '../../../../../../utils/nock';
import { SubmissionError } from '../../../../../../modules/form/submissionError';
import { ContestErrors } from '../../../../../../modules/api/uriel/contest';
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

    it('calls API', async () => {
      nockUriel()
        .get(`/contests/${contestJid}/problems`)
        .reply(200, responseBody);

      const response = await store.dispatch(contestProblemActions.getProblems(contestJid));
      expect(response).toEqual(responseBody);
    });
  });

  describe('setProblems()', () => {
    const data = [{ slug: 'slug1' }, { slug: 'slug2' }, { slug: 'slug3' }, { slug: 'slug4' }];

    describe('when all slugs are valid', () => {
      it('calls API', async () => {
        nockUriel()
          .options(`/contests/${contestJid}/problems`)
          .reply(200)
          .put(`/contests/${contestJid}/problems`, data)
          .reply(200);

        await store.dispatch(contestProblemActions.setProblems(contestJid, data));
      });
    });

    describe('when not all slugs are valid', () => {
      it('throws SubmissionError', async () => {
        nockUriel()
          .options(`/contests/${contestJid}/problems`)
          .reply(200)
          .put(`/contests/${contestJid}/problems`, data)
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

    it('calls API', async () => {
      nockUriel()
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

    it('calls API', async () => {
      nockUriel()
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
