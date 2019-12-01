import { selectToken } from '../../../../../../../modules/session/sessionSelectors';

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
        containerJid: contestJid,
        problemJid,
        itemJid,
        answer,
      };

      await contestSubmissionBundleAPI.createItemSubmission(token, data);
      toastActions.showToast('Answer saved.');
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

  regradeSubmission: (submissionJid: string) => {
    return async (dispatch, getState, { contestSubmissionBundleAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestSubmissionBundleAPI.regradeSubmission(token, submissionJid);

      toastActions.showSuccessToast('Submission regraded.');
    };
  },

  regradeSubmissions: (contestJid: string, userJid?: string, problemJid?: string) => {
    return async (dispatch, getState, { contestSubmissionBundleAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestSubmissionBundleAPI.regradeSubmissions(token, contestJid, userJid, problemJid);

      toastActions.showSuccessToast('Regrade in progress.');
    };
  },
};
