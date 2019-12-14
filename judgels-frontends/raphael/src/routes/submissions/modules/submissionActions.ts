import { selectToken } from '../../../modules/session/sessionSelectors';

export const submissionActions = {
  getSubmissions: (containerJid?: string, userJid?: string, problemJid?: string, page?: number) => {
    return async (dispatch, getState, { submissionProgrammingAPI }) => {
      const token = selectToken(getState());
      return await submissionProgrammingAPI.getSubmissions(token, containerJid, userJid, problemJid, page);
    };
  },

  getSubmissionWithSource: (submissionId: number, language?: string) => {
    return async (dispatch, getState, { submissionProgrammingAPI }) => {
      const token = selectToken(getState());
      const submissionWithSource = await submissionProgrammingAPI.getSubmissionWithSource(
        token,
        submissionId,
        language
      );
      return submissionWithSource;
    };
  },

  regradeSubmissions: (containerJid: string, userJid?: string, problemJid?: string) => {
    return async (dispatch, getState, { submissionProgrammingAPI, toastActions }) => {
      const token = selectToken(getState());
      await submissionProgrammingAPI.regradeSubmissions(token, containerJid, userJid, problemJid);

      toastActions.showSuccessToast('Regrade in progress.');
    };
  },
};
