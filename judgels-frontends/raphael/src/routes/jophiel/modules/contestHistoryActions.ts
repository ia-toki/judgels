export const contestHistoryActions = {
  getPublicHistory: (username: string) => {
    return async (dispatch, getState, { contestHistoryAPI }) => {
      return await contestHistoryAPI.getPublicHistory(username);
    };
  },
};
