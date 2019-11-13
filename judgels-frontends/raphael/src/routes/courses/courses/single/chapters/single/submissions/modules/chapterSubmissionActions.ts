import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { NotFoundError } from '../../../../../../../../modules/api/error';

export const chapterSubmissionActions = {
  getSubmissions: (chapterJid: string, userJid?: string, problemJid?: string, page?: number) => {
    return async (dispatch, getState, { chapterSubmissionProgrammingAPI }) => {
      const token = selectToken(getState());
      return await chapterSubmissionProgrammingAPI.getSubmissions(token, chapterJid, userJid, problemJid, page);
    };
  },

  getSubmissionWithSource: (chapterJid: string, submissionId: number, language?: string) => {
    return async (dispatch, getState, { chapterSubmissionProgrammingAPI }) => {
      const token = selectToken(getState());
      const submissionWithSource = await chapterSubmissionProgrammingAPI.getSubmissionWithSource(
        token,
        submissionId,
        language
      );
      if (chapterJid !== submissionWithSource.data.submission.containerJid) {
        throw new NotFoundError();
      }
      return submissionWithSource;
    };
  },
};
