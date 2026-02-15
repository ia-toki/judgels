import { submissionProgrammingAPI } from '../../../modules/api/jerahmeel/submissionProgramming';
import { getToken } from '../../../modules/session';
import { getWebPrefs } from '../../../modules/webPrefs';

import { toastActions } from '../../../modules/toast/toastActions';

export async function getSubmissions(containerJid, userJid, problemJid, page) {
  const token = getToken();
  return await submissionProgrammingAPI.getSubmissions(token, containerJid, userJid, problemJid, undefined, page);
}

export async function getSubmissionWithSource(submissionId, language) {
  const token = getToken();
  return await submissionProgrammingAPI.getSubmissionWithSource(token, submissionId, language);
}

export async function regradeSubmission(submissionJid) {
  const token = getToken();
  await submissionProgrammingAPI.regradeSubmission(token, submissionJid);

  toastActions.showSuccessToast('Submission regraded.');
}

export async function regradeSubmissions(containerJid, userJid, problemJid) {
  const token = getToken();
  await submissionProgrammingAPI.regradeSubmissions(token, containerJid, userJid, problemJid, undefined);

  toastActions.showSuccessToast('Regrade in progress.');
}

export async function getSubmissionSourceImage(submissionJid) {
  const { isDarkMode } = getWebPrefs();
  return await submissionProgrammingAPI.getSubmissionSourceImage(submissionJid, isDarkMode);
}
