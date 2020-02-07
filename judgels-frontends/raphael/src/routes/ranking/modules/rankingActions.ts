import { userStatsAPI } from '../../../modules/api/jerahmeel/user';

export const rankingActions = {
  getTopUserStats: (page?: number, pageSize?: number) => {
    return async (dispatch, getState) => {
      return await userStatsAPI.getTopUserStats(page, pageSize);
    };
  },
};
