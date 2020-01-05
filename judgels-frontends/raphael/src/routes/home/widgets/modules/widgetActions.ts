export const widgetActions = {
  getTopRatedProfiles: (page?: number, pageSize?: number) => {
    return async (dispatch, getState, { profileAPI }) => {
      return await profileAPI.getTopRatedProfiles(page, pageSize);
    };
  },

  getTopUserStats: (page?: number, pageSize?: number) => {
    return async (dispatch, getState, { userStatsAPI }) => {
      return await userStatsAPI.getTopUserStats(page, pageSize);
    };
  },
};
