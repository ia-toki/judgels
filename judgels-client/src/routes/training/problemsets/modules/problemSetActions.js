import { BadRequestError, ForbiddenError } from '../../../../modules/api/error';
import { ProblemSetErrors, problemSetAPI } from '../../../../modules/api/jerahmeel/problemSet';
import { problemSetProblemAPI } from '../../../../modules/api/jerahmeel/problemSetProblem';
import { SubmissionError } from '../../../../modules/form/submissionError';
import { selectToken } from '../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../modules/toast/toastActions';

export function createProblemSet(data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await problemSetAPI.createProblemSet(token, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemSetErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      if (error instanceof BadRequestError && error.message === ProblemSetErrors.ArchiveSlugNotFound) {
        throw new SubmissionError({ archiveSlug: 'Archive slug not found' });
      }
      throw error;
    }
    toastActions.showSuccessToast('Problemset created.');
  };
}

export function updateProblemSet(problemSetJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await problemSetAPI.updateProblemSet(token, problemSetJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemSetErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      if (error instanceof BadRequestError && error.message === ProblemSetErrors.ArchiveSlugNotFound) {
        throw new SubmissionError({ archiveSlug: 'Archive slug not found' });
      }
      throw error;
    }
    toastActions.showSuccessToast('Problemset updated.');
  };
}

export function getProblemSets(page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetAPI.getProblemSets(token, undefined, undefined, page);
  };
}

export function getProblems(problemSetJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetProblemAPI.getProblems(token, problemSetJid);
  };
}

export function setProblems(problemSetJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await problemSetProblemAPI.setProblems(token, problemSetJid, data);
    } catch (error) {
      if (error instanceof ForbiddenError && error.message === ProblemSetErrors.ContestSlugsNotAllowed) {
        const unknownSlugs = error.args.contestSlugs;
        throw new SubmissionError({ problems: 'Contests not found/allowed: ' + unknownSlugs });
      }
      throw error;
    }
    toastActions.showSuccessToast('Problemset problems updated.');
  };
}
