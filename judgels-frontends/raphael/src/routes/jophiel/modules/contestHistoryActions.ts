export const contestHistoryActions = {
  getHistory: (username: string) => {
    return async (dispatch, getState, { contestHistoryAPI }) => {
      return await contestHistoryAPI.getHistory(username);
    };
  },
};
