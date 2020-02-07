import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { submissionBundleAPI } from '../../../../../../../../modules/api/jerahmeel/submissionBundle';
import { toastActions } from '../../../../../../../../modules/toast/toastActions';

export const chapterSubmissionActions = {
  getSubmissions: (chapterJid: string, username?: string, problemAlias?: string, page?: number) => {
    return async (dispatch, getState) => {
      const token = selectToken(getState());
      return await submissionBundleAPI.getSubmissions(token, chapterJid, username, problemAlias, page);
    };
  },

  createItemSubmission: (chapterJid: string, problemJid: string, itemJid: string, answer: string) => {
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
  },

  getSubmissionSummary: (chapterJid: string, username?: string, language?: string) => {
    return async (dispatch, getState) => {
      const token = selectToken(getState());
      return submissionBundleAPI.getSubmissionSummary(token, chapterJid, undefined, username, language);
    };
  },

  getLatestSubmissions: (chapterJid: string, problemAlias: string) => {
    return async (dispatch, getState) => {
      const token = selectToken(getState());
      return submissionBundleAPI.getLatestSubmissions(token, chapterJid, problemAlias);
    };
  },

  regradeSubmission: (submissionJid: string) => {
    return async (dispatch, getState) => {
      const token = selectToken(getState());
      await submissionBundleAPI.regradeSubmission(token, submissionJid);

      toastActions.showSuccessToast('Submission regraded.');
    };
  },

  regradeSubmissions: (chapterJid: string, userJid?: string, problemJid?: string) => {
    return async (dispatch, getState) => {
      const token = selectToken(getState());
      await submissionBundleAPI.regradeSubmissions(token, chapterJid, userJid, problemJid);

      toastActions.showSuccessToast('Regrade in progress.');
    };
  },
};
