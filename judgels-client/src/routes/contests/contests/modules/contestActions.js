import { isTLX } from '../../../../conf';
import { BadRequestError, NotFoundError, RemoteError } from '../../../../modules/api/error';
import { problemSetAPI } from '../../../../modules/api/jerahmeel/problemSet';
import { ContestErrors, contestAPI } from '../../../../modules/api/uriel/contest';
import { SubmissionError } from '../../../../modules/form/submissionError';
import { getNavigationRef } from '../../../../modules/navigation/navigationRef';
import { getToken } from '../../../../modules/session';

import * as toastActions from '../../../../modules/toast/toastActions';

export async function createContest(data) {
  const token = getToken();
  try {
    await contestAPI.createContest(token, data);
  } catch (error) {
    if (error instanceof BadRequestError && error.message === ContestErrors.SlugAlreadyExists) {
      throw new SubmissionError({ slug: 'Slug already exists' });
    }
    throw error;
  }
  getNavigationRef().push(`/contests/${data.slug}`, { isEditingContest: true });
  toastActions.showSuccessToast('Contest created.');
}

export async function updateContest(contestJid, contestSlug, data) {
  const token = getToken();
  try {
    await contestAPI.updateContest(token, contestJid, data);
  } catch (error) {
    if (error instanceof BadRequestError && error.message === ContestErrors.SlugAlreadyExists) {
      throw new SubmissionError({ slug: 'Slug already exists' });
    }
    throw error;
  }
  toastActions.showSuccessToast('Contest updated.');

  if (data.slug && data.slug !== contestSlug) {
    getNavigationRef().push(`/contests/${data.slug}`);
  }
}

export async function getContests(name, page) {
  const token = getToken();
  return await contestAPI.getContests(token, name, page);
}

export async function getActiveContests() {
  const token = getToken();
  return await contestAPI.getActiveContests(token);
}

export async function getContestBySlug(contestSlug) {
  const token = getToken();
  const contest = await contestAPI.getContestBySlug(token, contestSlug);
  return contest;
}

export async function startVirtualContest(contestId) {
  const token = getToken();
  await contestAPI.startVirtualContest(token, contestId);
}

export async function getContestDescription(contestJid) {
  const token = getToken();
  return await contestAPI.getContestDescription(token, contestJid);
}

export async function updateContestDescription(contestJid, description) {
  const token = getToken();
  await contestAPI.updateContestDescription(token, contestJid, { description });
  toastActions.showSuccessToast('Description updated.');
}

export async function searchProblemSet(contestJid) {
  if (!isTLX()) {
    return await Promise.resolve(null);
  }
  try {
    return await problemSetAPI.searchProblemSet(contestJid);
  } catch (error) {
    if (error instanceof NotFoundError) {
      return await Promise.resolve(null);
    }
    if (error instanceof RemoteError) {
      return await Promise.resolve(null);
    }
    throw error;
  }
}
