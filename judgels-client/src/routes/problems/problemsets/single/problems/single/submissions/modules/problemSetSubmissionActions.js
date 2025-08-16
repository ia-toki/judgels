import { push } from 'connected-react-router';

import { getGradingLanguageEditorSubmissionFilename } from '../../../../../../../../modules/api/gabriel/language';
import { submissionProgrammingAPI } from '../../../../../../../../modules/api/jerahmeel/submissionProgramming';
import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { selectIsDarkMode } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';

import { toastActions } from '../../../../../../../../modules/toast/toastActions';

export function getSubmissions(problemSetJid, username, problemJid, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await submissionProgrammingAPI.getSubmissions(token, problemSetJid, username, problemJid, undefined, page);
  };
}

export function getSubmissionWithSource(submissionId, language) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await submissionProgrammingAPI.getSubmissionWithSource(token, submissionId, language);
  };
}

export function createSubmission(problemSetSlug, problemSetJid, problemAlias, problemJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
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
    dispatch(push(`/problems/${problemSetSlug}/${problemAlias}/submissions/mine`));
  };
}

export function regradeSubmission(submissionJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await submissionProgrammingAPI.regradeSubmission(token, submissionJid);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}

export function regradeSubmissions(problemSetJid, userJid, problemJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await submissionProgrammingAPI.regradeSubmissions(token, problemSetJid, userJid, problemJid);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}

export function getSubmissionSourceImage(submissionJid) {
  return async (dispatch, getState) => {
    const isDarkMode = selectIsDarkMode(getState());
    return await submissionProgrammingAPI.getSubmissionSourceImage(submissionJid, isDarkMode);
  };
}
