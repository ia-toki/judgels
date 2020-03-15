import { selectToken } from '../../../../../../../modules/session/sessionSelectors';
import { contestSubmissionBundleAPI } from '../../../../../../../modules/api/uriel/contestSubmissionBundle';
import * as toastActions from '../../../../../../../modules/toast/toastActions';

export function getSubmissions(contestJid: string, username?: string, problemAlias?: string, page?: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestSubmissionBundleAPI.getSubmissions(token, contestJid, username, problemAlias, page);
  };
}

export function createItemSubmission(contestJid: string, problemJid: string, itemJid: string, answer: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const data = {
      containerJid: contestJid,
      problemJid,
      itemJid,
      answer,
    };

    await contestSubmissionBundleAPI.createItemSubmission(token, data);
    toastActions.showToast('Answer saved.');
  };
}

export function getSubmissionSummary(contestJid: string, username?: string, language?: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return contestSubmissionBundleAPI.getSubmissionSummary(token, contestJid, username, language);
  };
}

export function getLatestSubmissions(contestJid: string, problemAlias: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return contestSubmissionBundleAPI.getLatestSubmissions(token, contestJid, problemAlias);
  };
}

export function regradeSubmission(submissionJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestSubmissionBundleAPI.regradeSubmission(token, submissionJid);

    toastActions.showSuccessToast('Submission regraded.');
  };
}

export function regradeSubmissions(contestJid: string, userJid?: string, problemJid?: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestSubmissionBundleAPI.regradeSubmissions(token, contestJid, userJid, problemJid);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}
