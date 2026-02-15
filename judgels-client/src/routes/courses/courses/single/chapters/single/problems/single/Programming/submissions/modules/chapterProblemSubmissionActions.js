import { submissionProgrammingAPI } from '../../../../../../../../../../../modules/api/jerahmeel/submissionProgramming';
import { getToken } from '../../../../../../../../../../../modules/session';
import { getWebPrefs } from '../../../../../../../../../../../modules/webPrefs';

import { toastActions } from '../../../../../../../../../../../modules/toast/toastActions';

export async function getSubmission(submissionJid) {
  return await submissionProgrammingAPI.getSubmission(submissionJid);
}

export async function getSubmissions(chapterJid, problemAlias, username, page) {
  const token = getToken();
  return await submissionProgrammingAPI.getSubmissions(token, chapterJid, username, undefined, problemAlias, page);
}

export async function getSubmissionWithSource(submissionId, language) {
  const token = getToken();
  return await submissionProgrammingAPI.getSubmissionWithSource(token, submissionId, language);
}

export async function getSubmissionSourceImage(submissionJid) {
  const { isDarkMode } = getWebPrefs();
  return await submissionProgrammingAPI.getSubmissionSourceImage(submissionJid, isDarkMode);
}

export async function createSubmission(chapterJid, problemJid, data) {
  const token = getToken();
  let sourceFiles = {};
  Object.keys(data.sourceFiles).forEach(key => {
    sourceFiles['sourceFiles.' + key] = data.sourceFiles[key];
  });

  return await submissionProgrammingAPI.createSubmission(
    token,
    chapterJid,
    problemJid,
    data.gradingLanguage,
    sourceFiles
  );
}

export async function regradeSubmission(submissionJid) {
  const token = getToken();
  await submissionProgrammingAPI.regradeSubmission(token, submissionJid);

  toastActions.showSuccessToast('Regrade in progress.');
}

export async function regradeSubmissions(chapterJid, username, problemAlias) {
  const token = getToken();
  await submissionProgrammingAPI.regradeSubmissions(token, chapterJid, username, undefined, problemAlias);

  toastActions.showSuccessToast('Regrade in progress.');
}
