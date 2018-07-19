import { selectToken } from '../../../../../../../../../../modules/session/sessionSelectors';

export const contestScoreboardActions = {
  getScoreboard: (contestJid: string) => {
    return async (dispatch, getState, { contestScoreboardAPI }) => {
      const token = selectToken(getState());
      return await contestScoreboardAPI.getScoreboard(token, contestJid);
    };
  },
};
