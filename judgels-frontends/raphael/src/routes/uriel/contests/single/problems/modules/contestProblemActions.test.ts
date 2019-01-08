import { SubmissionError } from 'redux-form';

import { contestJid, sessionState, token } from 'fixtures/state';
import { ForbiddenError } from 'modules/api/error';
import { ContestErrors } from 'modules/api/uriel/contest';
import { ContestProblemData, ContestProblemsResponse, ContestProblemWorksheet } from 'modules/api/uriel/contestProblem';
import { AppState } from 'modules/store';

import { contestProblemActions } from './contestProblemActions';

describe('contestProblemActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestProblemAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestProblemAPI = {
      getProblems: jest.fn(),
      setProblems: jest.fn(),
      getProblemWorksheet: jest.fn(),
    };
    toastActions = {
      showSuccessToast: jest.fn(),
    };
  });

  describe('getProblems()', () => {
    const { getProblems } = contestProblemActions;
    const doGetProblems = async () => getProblems(contestJid)(dispatch, getState, { contestProblemAPI });

    beforeEach(async () => {
      const problems = {} as ContestProblemsResponse;
      contestProblemAPI.getProblems.mockReturnValue(problems);

      await doGetProblems();
    });

    it('calls API to get problems', () => {
      expect(contestProblemAPI.getProblems).toHaveBeenCalledWith(token, contestJid);
    });
  });

  describe('setProblems()', () => {
    const { setProblems } = contestProblemActions;
    const data: ContestProblemData[] = [
      { slug: 'slug1' } as ContestProblemData,
      { slug: 'slug2' } as ContestProblemData,
      { slug: 'slug3' } as ContestProblemData,
      { slug: 'slug4' } as ContestProblemData,
    ];
    const doSetProblems = async () =>
      setProblems(contestJid, data)(dispatch, getState, { contestProblemAPI, toastActions });

    describe('when all slugs are valid', () => {
      beforeEach(async () => {
        contestProblemAPI.setProblems.mockReturnValue(Promise.resolve({}));

        await doSetProblems();
      });

      it('calls API to set problems', () => {
        expect(contestProblemAPI.setProblems).toHaveBeenCalledWith(token, contestJid, data);
      });

      it('shows success toast', () => {
        expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Problems updated.');
      });
    });

    describe('when not all slugs are valid', () => {
      beforeEach(async () => {
        contestProblemAPI.setProblems.mockImplementation(() => {
          throw new ForbiddenError({
            errorName: ContestErrors.ProblemSlugsNotAllowed,
            parameters: { slugs: 'slug2, slug4' },
          });
        });
      });

      it('throws SubmissionError', async () => {
        await expect(doSetProblems()).rejects.toEqual(
          new SubmissionError({ problems: 'Problems not found/allowed: slug2, slug4' })
        );
      });
    });
  });

  describe('getProblemWorksheet()', () => {
    const { getProblemWorksheet } = contestProblemActions;
    const doGetProblemWorksheet = async () =>
      getProblemWorksheet(contestJid, 'C', 'id')(dispatch, getState, { contestProblemAPI });

    beforeEach(async () => {
      const worksheet = {} as ContestProblemWorksheet;
      contestProblemAPI.getProblemWorksheet.mockReturnValue(worksheet);

      await doGetProblemWorksheet();
    });

    it('calls API to get problem worksheet', () => {
      expect(contestProblemAPI.getProblemWorksheet).toHaveBeenCalledWith(token, contestJid, 'C', 'id');
    });
  });
});
