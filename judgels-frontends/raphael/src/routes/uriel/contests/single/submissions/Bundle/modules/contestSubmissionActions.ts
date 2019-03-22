import { selectToken } from 'modules/session/sessionSelectors';

export const contestSubmissionActions = {
  getSubmissions: (contestJid: string, username?: string, problemAlias?: string, page?: number) => {
    return async (dispatch, getState, { contestSubmissionBundleAPI }) => {
      const token = selectToken(getState());
      return await contestSubmissionBundleAPI.getSubmissions(token, contestJid, username, problemAlias, page);
    };
  },

  createItemSubmission: (contestJid: string, problemJid: string, itemJid: string, answer: string) => {
    return async (dispatch, getState, { contestSubmissionBundleAPI, toastActions }) => {
      const token = selectToken(getState());
      const data = {
        contestJid,
        problemJid,
        itemJid,
        answer,
      };

      await contestSubmissionBundleAPI.createItemSubmission(token, data);
      toastActions.showSuccessToast('Solution submitted.');
    };
  },

  getSummary: (contestJid: string, username?: string, language?: string) => {
    return async (dispatch, getState, { contestSubmissionBundleAPI }) => {
      const token = selectToken(getState());
      return contestSubmissionBundleAPI.getAnswerSummaryForContestant(token, contestJid, username, language);
    };
  },

  getLatestSubmissions: (contestJid: string, problemAlias: string) => {
    return async (dispatch, getState, { contestSubmissionBundleAPI }) => {
      const token = selectToken(getState());
      return contestSubmissionBundleAPI.getLatestSubmissionsByUserForProblemInContest(token, contestJid, problemAlias);
    };
  },
};
