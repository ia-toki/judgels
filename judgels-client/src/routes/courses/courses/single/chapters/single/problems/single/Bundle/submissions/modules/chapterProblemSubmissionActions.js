import { selectToken } from '../../../../../../../../../../../modules/session/sessionSelectors';
import { submissionBundleAPI } from '../../../../../../../../../../../modules/api/jerahmeel/submissionBundle';
import { toastActions } from '../../../../../../../../../../../modules/toast/toastActions';

export function getSubmissions(chapterJid, username, problemAlias, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await submissionBundleAPI.getSubmissions(token, chapterJid, username, problemAlias, page);
  };
}

export function createItemSubmission(chapterJid, problemJid, itemJid, answer) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const data = {
      containerJid: chapterJid,
      problemJid,
      itemJid,
      answer,
    };

    await submissionBundleAPI.createItemSubmission(token, data);
    toastActions.showToast('Answer saved.');
  };
}

export function getSubmissionSummary(chapterJid, problemAlias, language) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return submissionBundleAPI.getSubmissionSummary(token, chapterJid, undefined, undefined, problemAlias, language);
  };
}

export function getLatestSubmissions(chapterJid, problemAlias) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return submissionBundleAPI.getLatestSubmissions(token, chapterJid, problemAlias);
  };
}
