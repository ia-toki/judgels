import { contestSubmissionBundleAPI } from '../../../../../../../modules/api/uriel/contestSubmissionBundle';
import { selectToken } from '../../../../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../../../../modules/toast/toastActions';

export function getSubmissions(contestJid, username, problemAlias, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestSubmissionBundleAPI.getSubmissions(token, contestJid, username, problemAlias, page);
  };
}

export function createItemSubmission(contestJid, problemJid, itemJid, answer) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const data = {
      containerJid: contestJid,
      problemJid,
      itemJid,
      answer,
    };

    await contestSubmissionBundleAPI.createItemSubmission(token, data);
  };
}

export function getSubmissionSummary(contestJid, username, language) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return contestSubmissionBundleAPI.getSubmissionSummary(token, contestJid, username, language);
  };
}

export function getLatestSubmissions(contestJid, problemAlias) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return contestSubmissionBundleAPI.getLatestSubmissions(token, contestJid, problemAlias);
  };
}

export function regradeSubmissions(contestJid, username, problemAlias) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestSubmissionBundleAPI.regradeSubmissions(token, contestJid, username, undefined, problemAlias);

    toastActions.showSuccessToast('Regraded.');
  };
}
