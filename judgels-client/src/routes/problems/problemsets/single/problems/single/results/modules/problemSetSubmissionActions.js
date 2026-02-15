import { submissionBundleAPI } from '../../../../../../../../modules/api/jerahmeel/submissionBundle';
import { getToken } from '../../../../../../../../modules/session';

import { toastActions } from '../../../../../../../../modules/toast/toastActions';

export async function getSubmissions(problemSetJid, username, problemAlias, page) {
  const token = getToken();
  return await submissionBundleAPI.getSubmissions(token, problemSetJid, username, problemAlias, page);
}

export async function createItemSubmission(problemSetJid, problemJid, itemJid, answer) {
  const token = getToken();
  const data = {
    containerJid: problemSetJid,
    problemJid,
    itemJid,
    answer,
  };

  await submissionBundleAPI.createItemSubmission(token, data);
}

export async function getSubmissionSummary(problemSetJid, problemJid, username, language) {
  const token = getToken();
  return submissionBundleAPI.getSubmissionSummary(token, problemSetJid, problemJid, username, language);
}

export async function getLatestSubmissions(problemSetJid, problemAlias) {
  const token = getToken();
  return submissionBundleAPI.getLatestSubmissions(token, problemSetJid, problemAlias);
}

export async function regradeSubmission(submissionJid) {
  const token = getToken();
  await submissionBundleAPI.regradeSubmission(token, submissionJid);

  toastActions.showSuccessToast('Submission regraded.');
}

export async function regradeSubmissions(problemSetJid, userJid, problemJid) {
  const token = getToken();
  await submissionBundleAPI.regradeSubmissions(token, problemSetJid, userJid, problemJid);

  toastActions.showSuccessToast('Regrade in progress.');
}
