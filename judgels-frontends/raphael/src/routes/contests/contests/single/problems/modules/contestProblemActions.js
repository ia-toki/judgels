import { SubmissionError } from 'redux-form';

import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { ForbiddenError } from '../../../../../../modules/api/error';
import { ContestErrors } from '../../../../../../modules/api/uriel/contest';
import { ContestProblemData, contestProblemAPI } from '../../../../../../modules/api/uriel/contestProblem';
import * as toastActions from '../../../../../../modules/toast/toastActions';

export function getProblems(contestJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestProblemAPI.getProblems(token, contestJid);
  };
}

export function setProblems(contestJid: string, data: ContestProblemData[]) {
  return async (dispatch, getState) => {
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
}

export function getBundleProblemWorksheet(contestJid: string, problemAlias: string, language?: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestProblemAPI.getBundleProblemWorksheet(token, contestJid, problemAlias, language);
  };
}

export function getProgrammingProblemWorksheet(contestJid: string, problemAlias: string, language?: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestProblemAPI.getProgrammingProblemWorksheet(token, contestJid, problemAlias, language);
  };
}
