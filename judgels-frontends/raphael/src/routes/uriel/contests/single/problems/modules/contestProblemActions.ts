import { SubmissionError } from 'redux-form';

import { selectToken } from 'modules/session/sessionSelectors';
import { ForbiddenError, NotFoundError } from 'modules/api/error';
import { ContestErrors } from 'modules/api/uriel/contest';
import { ContestProblemData } from 'modules/api/uriel/contestProblem';

export const contestProblemActions = {
  getProblems: (contestJid: string) => {
    return async (dispatch, getState, { contestProblemAPI }) => {
      const token = selectToken(getState());
      return await contestProblemAPI.getProblems(token, contestJid);
    };
  },

  setProblems: (contestJid: string, data: ContestProblemData[]) => {
    return async (dispatch, getState, { contestProblemAPI, toastActions }) => {
      const token = selectToken(getState());

      try {
        await contestProblemAPI.setProblems(token, contestJid, data);
      } catch (error) {
        if (error instanceof ForbiddenError && error.message === ContestErrors.ProblemSlugsNotAllowed) {
          const unknownSlugs = error.parameters.slugs;
          throw new SubmissionError({ problems: 'Problems not found/allowed: ' + unknownSlugs });
        }
        throw error;
      }

      toastActions.showSuccessToast('Problems updated.');
    };
  },

  getProblemInfo: (contestJid: string, problemAlias: string) => {
    return async (dispatch, getState, { contestProblemAPI }) => {
      const token = selectToken(getState());

      const problemsResponse = await contestProblemAPI.getProblems(token, contestJid);
      const currentProblem = problemsResponse.data.filter(prob => prob.alias === problemAlias).pop();
      if (!currentProblem) {
        throw new NotFoundError();
      }
      const problemJid = currentProblem.problemJid;
      const problemInfo = problemsResponse.problemsMap[problemJid];
      if (!problemInfo) {
        throw new NotFoundError();
      }
      return problemInfo;
    };
  },

  getBundleProblemWorksheet: (contestJid: string, problemAlias: string, language?: string) => {
    return async (dispatch, getState, { contestProblemAPI }) => {
      const token = selectToken(getState());
      return await contestProblemAPI.getBundleProblemWorksheet(token, contestJid, problemAlias, language);
    };
  },

  getProgrammingProblemWorksheet: (contestJid: string, problemAlias: string, language?: string) => {
    return async (dispatch, getState, { contestProblemAPI }) => {
      const token = selectToken(getState());
      return await contestProblemAPI.getProgrammingProblemWorksheet(token, contestJid, problemAlias, language);
    };
  },
};
