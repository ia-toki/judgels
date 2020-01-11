export const rankingActions = {
  getTopUserStats: (page?: number, pageSize?: number) => {
    return async (dispatch, getState, { userStatsAPI }) => {
      return await userStatsAPI.getTopUserStats(page, pageSize);
    };
  },
};
