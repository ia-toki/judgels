import { ForbiddenError } from '../../../../../../modules/api/error';
import { ContestErrors } from '../../../../../../modules/api/uriel/contest';
import { contestProblemAPI } from '../../../../../../modules/api/uriel/contestProblem';
import { SubmissionError } from '../../../../../../modules/form/submissionError';
import { getToken } from '../../../../../../modules/session';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export async function getProblems(contestJid) {
  const token = getToken();
  return await contestProblemAPI.getProblems(token, contestJid);
}

export async function setProblems(contestJid, data) {
  const token = getToken();

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
}

export async function getBundleProblemWorksheet(contestJid, problemAlias, language) {
  const token = getToken();
  return await contestProblemAPI.getBundleProblemWorksheet(token, contestJid, problemAlias, language);
}

export async function getProgrammingProblemWorksheet(contestJid, problemAlias, language) {
  const token = getToken();
  return await contestProblemAPI.getProgrammingProblemWorksheet(token, contestJid, problemAlias, language);
}
