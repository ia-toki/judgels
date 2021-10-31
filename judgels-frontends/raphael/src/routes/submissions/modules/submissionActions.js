import { selectToken } from '../../../modules/session/sessionSelectors';
import { selectIsDarkMode } from '../../../modules/webPrefs/webPrefsSelectors';
import { submissionProgrammingAPI } from '../../../modules/api/jerahmeel/submissionProgramming';
import { toastActions } from '../../../modules/toast/toastActions';

export function getSubmissions(containerJid, userJid, problemJid, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await submissionProgrammingAPI.getSubmissions(token, containerJid, userJid, problemJid, undefined, page);
  };
}

export function getSubmissionWithSource(submissionId, language) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await submissionProgrammingAPI.getSubmissionWithSource(token, submissionId, language);
  };
}

export function regradeSubmission(submissionJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await submissionProgrammingAPI.regradeSubmission(token, submissionJid);

    toastActions.showSuccessToast('Submission regraded.');
  };
}

export function regradeSubmissions(containerJid, userJid, problemJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await submissionProgrammingAPI.regradeSubmissions(token, containerJid, userJid, problemJid, undefined);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}

export function getSubmissionSourceImage(submissionJid) {
  return async (dispatch, getState) => {
    const isDarkMode = selectIsDarkMode(getState());
    return await submissionProgrammingAPI.getSubmissionSourceImage(submissionJid, isDarkMode);
  };
}
