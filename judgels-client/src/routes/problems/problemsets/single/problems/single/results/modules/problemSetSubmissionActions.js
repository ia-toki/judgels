import { submissionBundleAPI } from '../../../../../../../../modules/api/jerahmeel/submissionBundle';
import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';

import { toastActions } from '../../../../../../../../modules/toast/toastActions';

export function getSubmissions(problemSetJid, username, problemAlias, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await submissionBundleAPI.getSubmissions(token, problemSetJid, username, problemAlias, page);
  };
}

export function createItemSubmission(problemSetJid, problemJid, itemJid, answer) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const data = {
      containerJid: problemSetJid,
      problemJid,
      itemJid,
      answer,
    };

    await submissionBundleAPI.createItemSubmission(token, data);
    toastActions.showToast('Answer saved.');
  };
}

export function getSubmissionSummary(problemSetJid, problemJid, username, language) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return submissionBundleAPI.getSubmissionSummary(token, problemSetJid, problemJid, username, language);
  };
}

export function getLatestSubmissions(problemSetJid, problemAlias) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return submissionBundleAPI.getLatestSubmissions(token, problemSetJid, problemAlias);
  };
}

export function regradeSubmission(submissionJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await submissionBundleAPI.regradeSubmission(token, submissionJid);

    toastActions.showSuccessToast('Submission regraded.');
  };
}

export function regradeSubmissions(problemSetJid, userJid, problemJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await submissionBundleAPI.regradeSubmissions(token, problemSetJid, userJid, problemJid);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}
