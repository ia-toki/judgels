import { submissionProgrammingAPI } from '../../../../../../../../../../../modules/api/jerahmeel/submissionProgramming';
import { selectToken } from '../../../../../../../../../../../modules/session/sessionSelectors';
import { selectIsDarkMode } from '../../../../../../../../../../../modules/webPrefs/webPrefsSelectors';

import { toastActions } from '../../../../../../../../../../../modules/toast/toastActions';

export function getSubmissions(chapterJid, problemAlias, username, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await submissionProgrammingAPI.getSubmissions(token, chapterJid, username, undefined, problemAlias, page);
  };
}

export function getSubmissionWithSource(submissionId, language) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await submissionProgrammingAPI.getSubmissionWithSource(token, submissionId, language);
  };
}

export function getSubmissionSourceImage(submissionJid) {
  return async (dispatch, getState) => {
    const isDarkMode = selectIsDarkMode(getState());
    return await submissionProgrammingAPI.getSubmissionSourceImage(submissionJid, isDarkMode);
  };
}

export function createSubmission(chapterJid, problemJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
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
  };
}

export function regradeSubmission(submissionJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await submissionProgrammingAPI.regradeSubmission(token, submissionJid);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}

export function regradeSubmissions(chapterJid, username, problemAlias) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await submissionProgrammingAPI.regradeSubmissions(token, chapterJid, username, undefined, problemAlias);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}
