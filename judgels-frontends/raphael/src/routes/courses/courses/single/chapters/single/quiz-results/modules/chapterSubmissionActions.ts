import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';

export const chapterSubmissionActions = {
  getSubmissions: (chapterJid: string, username?: string, problemAlias?: string, page?: number) => {
    return async (dispatch, getState, { chapterSubmissionBundleAPI }) => {
      const token = selectToken(getState());
      return await chapterSubmissionBundleAPI.getSubmissions(token, chapterJid, username, problemAlias, page);
    };
  },

  createItemSubmission: (chapterJid: string, problemJid: string, itemJid: string, answer: string) => {
    return async (dispatch, getState, { chapterSubmissionBundleAPI, toastActions }) => {
      const token = selectToken(getState());
      const data = {
        containerJid: chapterJid,
        problemJid,
        itemJid,
        answer,
      };

      await chapterSubmissionBundleAPI.createItemSubmission(token, data);
      toastActions.showToast('Answer saved.');
    };
  },

  getSummary: (chapterJid: string, username?: string, language?: string) => {
    return async (dispatch, getState, { chapterSubmissionBundleAPI }) => {
      const token = selectToken(getState());
      return chapterSubmissionBundleAPI.getAnswerSummary(token, chapterJid, username, language);
    };
  },

  getLatestSubmissions: (chapterJid: string, problemAlias: string) => {
    return async (dispatch, getState, { chapterSubmissionBundleAPI }) => {
      const token = selectToken(getState());
      return chapterSubmissionBundleAPI.getLatestSubmissionsByUserForProblemInChapter(token, chapterJid, problemAlias);
    };
  },

  regradeSubmission: (submissionJid: string) => {
    return async (dispatch, getState, { chapterSubmissionBundleAPI, toastActions }) => {
      const token = selectToken(getState());
      await chapterSubmissionBundleAPI.regradeSubmission(token, submissionJid);

      toastActions.showSuccessToast('Submission regraded.');
    };
  },

  regradeSubmissions: (chapterJid: string, userJid?: string, problemJid?: string) => {
    return async (dispatch, getState, { chapterSubmissionBundleAPI, toastActions }) => {
      const token = selectToken(getState());
      await chapterSubmissionBundleAPI.regradeSubmissions(token, chapterJid, userJid, problemJid);

      toastActions.showSuccessToast('Regrade in progress.');
    };
  },
};
