import { selectToken } from '../../../../../../../../../modules/session/sessionSelectors';

export const chapterSubmissionActions = {
  getSubmissions: (chapterJid: string, userJid?: string, problemJid?: string, page?: number) => {
    return async (dispatch, getState, { chapterSubmissionProgrammingAPI }) => {
      const token = selectToken(getState());
      return await chapterSubmissionProgrammingAPI.getSubmissions(token, chapterJid, userJid, problemJid, page);
    };
  },
};
