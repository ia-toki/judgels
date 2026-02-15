import nock from 'nock';

import { ContestErrors } from '../../../../../../modules/api/uriel/contest';
import { SubmissionError } from '../../../../../../modules/form/submissionError';
import { nockUriel } from '../../../../../../utils/nock';

import * as contestProblemActions from './contestProblemActions';

const contestJid = 'contest-jid';

describe('contestProblemActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('getProblems()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API', async () => {
      nockUriel().get(`/contests/${contestJid}/problems`).reply(200, responseBody);

      const response = await contestProblemActions.getProblems(contestJid);
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

        await contestProblemActions.setProblems(contestJid, data);
      });
    });

    describe('when not all slugs are valid', () => {
      it('throws SubmissionError', async () => {
        nockUriel()
          .options(`/contests/${contestJid}/problems`)
          .reply(200)
          .put(`/contests/${contestJid}/problems`, data)
          .reply(403, {
            message: ContestErrors.ProblemSlugsNotAllowed,
            args: { slugs: 'slug2, slug4' },
          });

        await expect(contestProblemActions.setProblems(contestJid, data)).rejects.toEqual(
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

      const response = await contestProblemActions.getBundleProblemWorksheet(contestJid, problemAlias, language);
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

      const response = await contestProblemActions.getProgrammingProblemWorksheet(contestJid, problemAlias, language);
      expect(response).toEqual(responseBody);
    });
  });
});
