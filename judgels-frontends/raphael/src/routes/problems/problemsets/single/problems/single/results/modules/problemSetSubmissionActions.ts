import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';

export const problemSetSubmissionActions = {
  getSubmissions: (problemSetJid: string, username?: string, problemAlias?: string, page?: number) => {
    return async (dispatch, getState, { submissionBundleAPI }) => {
      const token = selectToken(getState());
      return await submissionBundleAPI.getSubmissions(token, problemSetJid, username, problemAlias, page);
    };
  },

  createItemSubmission: (problemSetJid: string, problemJid: string, itemJid: string, answer: string) => {
    return async (dispatch, getState, { submissionBundleAPI, toastActions }) => {
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
  },

  getSubmissionSummary: (problemSetJid: string, problemJid: string, username?: string, language?: string) => {
    return async (dispatch, getState, { submissionBundleAPI }) => {
      const token = selectToken(getState());
      return submissionBundleAPI.getSubmissionSummary(token, problemSetJid, problemJid, username, language);
    };
  },

  getLatestSubmissions: (problemSetJid: string, problemAlias: string) => {
    return async (dispatch, getState, { submissionBundleAPI }) => {
      const token = selectToken(getState());
      return submissionBundleAPI.getLatestSubmissions(token, problemSetJid, problemAlias);
    };
  },

  regradeSubmission: (submissionJid: string) => {
    return async (dispatch, getState, { submissionBundleAPI, toastActions }) => {
      const token = selectToken(getState());
      await submissionBundleAPI.regradeSubmission(token, submissionJid);

      toastActions.showSuccessToast('Submission regraded.');
    };
  },

  regradeSubmissions: (problemSetJid: string, userJid?: string, problemJid?: string) => {
    return async (dispatch, getState, { submissionBundleAPI, toastActions }) => {
      const token = selectToken(getState());
      await submissionBundleAPI.regradeSubmissions(token, problemSetJid, userJid, problemJid);

      toastActions.showSuccessToast('Regrade in progress.');
    };
  },
};
