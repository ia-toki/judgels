import { contestSubmissionBundleAPI } from '../../../../../../../modules/api/uriel/contestSubmissionBundle';
import { getToken } from '../../../../../../../modules/session';

import * as toastActions from '../../../../../../../modules/toast/toastActions';

export async function getSubmissions(contestJid, username, problemAlias, page) {
  const token = getToken();
  return await contestSubmissionBundleAPI.getSubmissions(token, contestJid, username, problemAlias, page);
}

export async function createItemSubmission(contestJid, problemJid, itemJid, answer) {
  const token = getToken();
  const data = {
    containerJid: contestJid,
    problemJid,
    itemJid,
    answer,
  };

  await contestSubmissionBundleAPI.createItemSubmission(token, data);
}

export async function getSubmissionSummary(contestJid, username, language) {
  const token = getToken();
  return contestSubmissionBundleAPI.getSubmissionSummary(token, contestJid, username, language);
}

export async function getLatestSubmissions(contestJid, problemAlias) {
  const token = getToken();
  return contestSubmissionBundleAPI.getLatestSubmissions(token, contestJid, problemAlias);
}

export async function regradeSubmissions(contestJid, username, problemAlias) {
  const token = getToken();
  await contestSubmissionBundleAPI.regradeSubmissions(token, contestJid, username, undefined, problemAlias);

  toastActions.showSuccessToast('Regraded.');
}
