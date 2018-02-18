export const contestActions = {
  fetchList: (page: number) => {
    return async (dispatch, getState, { contestAPI }) => {
      return await contestAPI.getContests(page, 20);
    };
  },
};
