import { selectToken } from '../../../../../../../../../../modules/session/sessionSelectors';

export const contestSubmissionActions = {
  fetchMyList: (contestJid: string, page: number) => {
    return async (dispatch, getState, { contestSubmissionAPI }) => {
      const token = selectToken(getState());
      return await contestSubmissionAPI.getMySubmissions(token, contestJid, page);
    };
  },
};
