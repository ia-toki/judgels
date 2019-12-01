import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';

export const problemSetSubmissionActions = {
  getSubmissions: (problemSetJid: string, username?: string, problemAlias?: string, page?: number) => {
    return async (dispatch, getState, { problemSetSubmissionBundleAPI }) => {
      const token = selectToken(getState());
      return await problemSetSubmissionBundleAPI.getSubmissions(token, problemSetJid, username, problemAlias, page);
    };
  },

  createItemSubmission: (problemSetJid: string, problemJid: string, itemJid: string, answer: string) => {
    return async (dispatch, getState, { problemSetSubmissionBundleAPI, toastActions }) => {
      const token = selectToken(getState());
      const data = {
        containerJid: problemSetJid,
        problemJid,
        itemJid,
        answer,
      };

      await problemSetSubmissionBundleAPI.createItemSubmission(token, data);
      toastActions.showToast('Answer saved.');
    };
  },

  getSummary: (problemSetJid: string, username?: string, language?: string) => {
    return async (dispatch, getState, { problemSetSubmissionBundleAPI }) => {
      const token = selectToken(getState());
      return problemSetSubmissionBundleAPI.getAnswerSummary(token, problemSetJid, username, language);
    };
  },

  getLatestSubmissions: (problemSetJid: string, problemAlias: string) => {
    return async (dispatch, getState, { problemSetSubmissionBundleAPI }) => {
      const token = selectToken(getState());
      return problemSetSubmissionBundleAPI.getLatestSubmissionsByUserForProblemInProblemSet(
        token,
        problemSetJid,
        problemAlias
      );
    };
  },

  regradeSubmission: (submissionJid: string) => {
    return async (dispatch, getState, { problemSetSubmissionBundleAPI, toastActions }) => {
      const token = selectToken(getState());
      await problemSetSubmissionBundleAPI.regradeSubmission(token, submissionJid);

      toastActions.showSuccessToast('Submission regraded.');
    };
  },

  regradeSubmissions: (problemSetJid: string, userJid?: string, problemJid?: string) => {
    return async (dispatch, getState, { problemSetSubmissionBundleAPI, toastActions }) => {
      const token = selectToken(getState());
      await problemSetSubmissionBundleAPI.regradeSubmissions(token, problemSetJid, userJid, problemJid);

      toastActions.showSuccessToast('Regrade in progress.');
    };
  },
};
