import { getGradingLanguageEditorSubmissionFilename } from '../../../../../../../../modules/api/gabriel/language';
import { submissionProgrammingAPI } from '../../../../../../../../modules/api/jerahmeel/submissionProgramming';
import { getNavigationRef } from '../../../../../../../../modules/navigation/navigationRef';
import { getToken } from '../../../../../../../../modules/session';
import { getWebPrefs } from '../../../../../../../../modules/webPrefs';

import { toastActions } from '../../../../../../../../modules/toast/toastActions';

export async function getSubmissions(problemSetJid, username, problemJid, page) {
  const token = getToken();
  return await submissionProgrammingAPI.getSubmissions(token, problemSetJid, username, problemJid, undefined, page);
}

export async function getSubmissionWithSource(submissionId, language) {
  const token = getToken();
  return await submissionProgrammingAPI.getSubmissionWithSource(token, submissionId, language);
}

export async function createSubmission(problemSetSlug, problemSetJid, problemAlias, problemJid, data) {
  const token = getToken();
  let sources = {};
  Object.keys(data.sourceTexts ?? []).forEach(key => {
    sources['sourceFiles.' + key] = new File(
      [data.sourceTexts[key]],
      getGradingLanguageEditorSubmissionFilename(data.gradingLanguage),
      { type: 'text/plain' }
    );
  });
  Object.keys(data.sourceFiles ?? []).forEach(key => {
    sources['sourceFiles.' + key] = data.sourceFiles[key];
  });

  await submissionProgrammingAPI.createSubmission(token, problemSetJid, problemJid, data.gradingLanguage, sources);

  toastActions.showSuccessToast('Solution submitted.');

  window.scrollTo(0, 0);
  getNavigationRef().push(`/problems/${problemSetSlug}/${problemAlias}/submissions/mine`);
}

export async function regradeSubmission(submissionJid) {
  const token = getToken();
  await submissionProgrammingAPI.regradeSubmission(token, submissionJid);

  toastActions.showSuccessToast('Regrade in progress.');
}

export async function regradeSubmissions(problemSetJid, userJid, problemJid) {
  const token = getToken();
  await submissionProgrammingAPI.regradeSubmissions(token, problemSetJid, userJid, problemJid);

  toastActions.showSuccessToast('Regrade in progress.');
}

export async function getSubmissionSourceImage(submissionJid) {
  const { isDarkMode } = getWebPrefs();
  return await submissionProgrammingAPI.getSubmissionSourceImage(submissionJid, isDarkMode);
}
