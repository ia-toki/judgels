import { NotFoundError } from '../../../../../../../modules/api/error';
import { getGradingLanguageEditorSubmissionFilename } from '../../../../../../../modules/api/gabriel/language';
import { contestSubmissionProgrammingAPI } from '../../../../../../../modules/api/uriel/contestSubmissionProgramming';
import { getNavigationRef } from '../../../../../../../modules/navigation/navigationRef';
import { getToken } from '../../../../../../../modules/session';

import * as toastActions from '../../../../../../../modules/toast/toastActions';

export async function getSubmissions(contestJid, username, problemAlias, page) {
  const token = getToken();
  return await contestSubmissionProgrammingAPI.getSubmissions(token, contestJid, username, problemAlias, page);
}

export async function getSubmissionWithSource(contestJid, submissionId, language) {
  const token = getToken();
  const submissionWithSource = await contestSubmissionProgrammingAPI.getSubmissionWithSource(
    token,
    submissionId,
    language
  );
  if (contestJid !== submissionWithSource.data.submission.containerJid) {
    throw new NotFoundError();
  }
  return submissionWithSource;
}

export async function createSubmission(contestJid, contestSlug, problemJid, data) {
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

  await contestSubmissionProgrammingAPI.createSubmission(token, contestJid, problemJid, data.gradingLanguage, sources);

  toastActions.showSuccessToast('Solution submitted.');

  window.scrollTo(0, 0);
  getNavigationRef().push(`/contests/${contestSlug}/submissions`);
}

export async function regradeSubmission(submissionJid) {
  const token = getToken();
  await contestSubmissionProgrammingAPI.regradeSubmission(token, submissionJid);

  toastActions.showSuccessToast('Regrade in progress.');
}

export async function regradeSubmissions(contestJid, username, problemAlias) {
  const token = getToken();
  await contestSubmissionProgrammingAPI.regradeSubmissions(token, contestJid, username, problemAlias);

  toastActions.showSuccessToast('Regrade in progress.');
}

export async function downloadSubmission(submissionJid) {
  const token = getToken();
  await contestSubmissionProgrammingAPI.downloadSubmission(token, submissionJid);
}
