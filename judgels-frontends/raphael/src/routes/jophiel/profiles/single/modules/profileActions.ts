import { selectToken } from '../../../../../modules/session/sessionSelectors';

export const profileActions = {
  getBasicProfile: (userJid: string) => {
    return async (dispatch, getState, { profileAPI }) => {
      return await profileAPI.getBasicProfile(userJid);
    };
  },

  getUserStats: (username: string) => {
    return async (dispatch, getState, { userStatsAPI }) => {
      return await userStatsAPI.getUserStats(username);
    };
  },

  getContestPublicHistory: (username: string) => {
    return async (dispatch, getState, { contestHistoryAPI }) => {
      return await contestHistoryAPI.getPublicHistory(username);
    };
  },

  getSubmissions: (userJid: string, page?: number) => {
    return async (dispatch, getState, { submissionProgrammingAPI }) => {
      const token = selectToken(getState());
      return await submissionProgrammingAPI.getSubmissions(token, undefined, userJid, undefined, page);
    };
  },
};
