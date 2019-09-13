export const contestRatingActions = {
  getRatingHistory: (username: string) => {
    return async (dispatch, getState, { contestRatingAPI }) => {
      return await contestRatingAPI.getRatingHistory(username);
    };
  },
};
