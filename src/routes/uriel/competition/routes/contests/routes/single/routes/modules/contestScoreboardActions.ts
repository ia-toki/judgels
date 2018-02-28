export const contestScoreboardActions = {
  get: (contestJid: string) => {
    return async (dispatch, getState, { contestScoreboardAPI }) => {
      return await contestScoreboardAPI.getScoreboard(contestJid);
    };
  },
};
