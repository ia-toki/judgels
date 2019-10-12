import { selectToken } from '../../../../../../modules/session/sessionSelectors';

export const contestScoreboardActions = {
  getScoreboard: (contestJid: string, frozen?: boolean, showClosedProblems?: boolean, page?: number) => {
    return async (dispatch, getState, { contestScoreboardAPI }) => {
      const token = selectToken(getState());
      return await contestScoreboardAPI.getScoreboard(token, contestJid, frozen, showClosedProblems, page);
    };
  },

  refreshScoreboard: (contestJid: string) => {
    return async (dispatch, getState, { contestScoreboardAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestScoreboardAPI.refreshScoreboard(token, contestJid);
      toastActions.showSuccessToast('Scoreboard refresh requested.');
    };
  },
};
