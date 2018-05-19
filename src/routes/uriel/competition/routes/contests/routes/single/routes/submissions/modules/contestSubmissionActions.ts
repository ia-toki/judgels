import { selectToken } from '../../../../../../../../../../modules/session/sessionSelectors';
import { NotFoundError } from '../../../../../../../../../../modules/api/error';

export const contestSubmissionActions = {
  fetchMyList: (contestJid: string, page: number) => {
    return async (dispatch, getState, { contestSubmissionAPI }) => {
      const token = selectToken(getState());
      return await contestSubmissionAPI.getMySubmissions(token, contestJid, page);
    };
  },

  fetchWithSource: (contestJid: string, submissionId: number) => {
    return async (dispatch, getState, { contestSubmissionAPI }) => {
      const token = selectToken(getState());
      const submissionWithSource = await contestSubmissionAPI.getSubmissionWithSource(token, submissionId);
      if (contestJid !== submissionWithSource.data.submission.containerJid) {
        throw new NotFoundError();
      }
      return submissionWithSource;
    };
  },
};
