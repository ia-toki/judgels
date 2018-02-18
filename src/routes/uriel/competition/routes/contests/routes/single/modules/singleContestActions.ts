export const singleContestActions = {
  get: (contestJid: string) => {
    return async (dispatch, getState, { contestAPI }) => {
      return await contestAPI.getContest(contestJid);
    };
  },
};
