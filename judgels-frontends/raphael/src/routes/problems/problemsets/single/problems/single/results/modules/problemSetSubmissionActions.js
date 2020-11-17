import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { submissionBundleAPI } from '../../../../../../../../modules/api/jerahmeel/submissionBundle';
import { toastActions } from '../../../../../../../../modules/toast/toastActions';

export function getSubmissions(problemSetJid: string, username?: string, problemAlias?: string, page?: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await submissionBundleAPI.getSubmissions(token, problemSetJid, username, problemAlias, page);
  };
}

export function createItemSubmission(problemSetJid: string, problemJid: string, itemJid: string, answer: string) {
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

export function getSubmissionSummary(problemSetJid: string, problemJid: string, username?: string, language?: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return submissionBundleAPI.getSubmissionSummary(token, problemSetJid, problemJid, username, language);
  };
}

export function getLatestSubmissions(problemSetJid: string, problemAlias: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return submissionBundleAPI.getLatestSubmissions(token, problemSetJid, problemAlias);
  };
}

export function regradeSubmission(submissionJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await submissionBundleAPI.regradeSubmission(token, submissionJid);

    toastActions.showSuccessToast('Submission regraded.');
  };
}

export function regradeSubmissions(problemSetJid: string, userJid?: string, problemJid?: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await submissionBundleAPI.regradeSubmissions(token, problemSetJid, userJid, problemJid);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}
