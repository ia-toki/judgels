import { selectToken } from 'modules/session/sessionSelectors';

export const contestBundleSubmissionActions = {
  getSubmissions: (contestJid: string, userJid?: string, problemJid?: string, page?: number) => {
    return async (dispatch, getState, { contestSubmissionBundleAPI }) => {
      const token = selectToken(getState());
      return await contestSubmissionBundleAPI.getSubmissions(token, contestJid, userJid, problemJid, page);
    };
  },

  createItemSubmission: (contestJid: string, problemJid: string, itemJid: string, answer?: string) => {
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
};
