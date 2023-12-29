import { ForbiddenError } from '../../../../../../modules/api/error';
import { ContestErrors } from '../../../../../../modules/api/uriel/contest';
import { contestProblemAPI } from '../../../../../../modules/api/uriel/contestProblem';
import { SubmissionError } from '../../../../../../modules/form/submissionError';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export function getProblems(contestJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestProblemAPI.getProblems(token, contestJid);
  };
}

export function setProblems(contestJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());

    try {
      await contestProblemAPI.setProblems(token, contestJid, data);
    } catch (error) {
      if (error instanceof ForbiddenError && error.message === ContestErrors.ProblemSlugsNotAllowed) {
        const unknownSlugs = error.args.slugs;
        throw new SubmissionError({ problems: 'Problems not found/allowed: ' + unknownSlugs });
      }
      throw error;
    }

    toastActions.showSuccessToast('Problems updated.');
  };
}

export function getBundleProblemWorksheet(contestJid, problemAlias, language) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestProblemAPI.getBundleProblemWorksheet(token, contestJid, problemAlias, language);
  };
}

export function getProgrammingProblemWorksheet(contestJid, problemAlias, language) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestProblemAPI.getProgrammingProblemWorksheet(token, contestJid, problemAlias, language);
  };
}
