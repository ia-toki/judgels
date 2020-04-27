import { SubmissionError } from 'redux-form';

import { selectToken } from '../../../../modules/session/sessionSelectors';
import { BadRequestError } from '../../../../modules/api/error';
import {
  problemSetAPI,
  ProblemSetCreateData,
  ProblemSetUpdateData,
  ProblemSetErrors,
} from '../../../../modules/api/jerahmeel/problemSet';
import * as toastActions from '../../../../modules/toast/toastActions';

export function createProblemSet(data: ProblemSetCreateData) {
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

export function updateProblemSet(problemSetJid: string, data: ProblemSetUpdateData) {
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

export function getProblemSets(page: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetAPI.getProblemSets(token, undefined, undefined, page);
  };
}
