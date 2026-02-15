import { BadRequestError, ForbiddenError } from '../../../../modules/api/error';
import { ProblemSetErrors, problemSetAPI } from '../../../../modules/api/jerahmeel/problemSet';
import { problemSetProblemAPI } from '../../../../modules/api/jerahmeel/problemSetProblem';
import { SubmissionError } from '../../../../modules/form/submissionError';
import { getToken } from '../../../../modules/session';

import * as toastActions from '../../../../modules/toast/toastActions';

export async function createProblemSet(data) {
  const token = getToken();
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
}

export async function updateProblemSet(problemSetJid, data) {
  const token = getToken();
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
}

export async function getProblemSets(page) {
  const token = getToken();
  return await problemSetAPI.getProblemSets(token, undefined, undefined, page);
}

export async function getProblems(problemSetJid) {
  const token = getToken();
  return await problemSetProblemAPI.getProblems(token, problemSetJid);
}

export async function setProblems(problemSetJid, data) {
  const token = getToken();
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
}
