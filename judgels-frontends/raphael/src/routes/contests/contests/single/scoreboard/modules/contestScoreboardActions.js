import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { contestScoreboardAPI } from '../../../../../../modules/api/uriel/contestScoreboard';
import * as toastActions from '../../../../../../modules/toast/toastActions';

export function getScoreboard(contestJid: string, frozen?: boolean, showClosedProblems?: boolean, page?: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestScoreboardAPI.getScoreboard(token, contestJid, frozen, showClosedProblems, page);
  };
}

export function refreshScoreboard(contestJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestScoreboardAPI.refreshScoreboard(token, contestJid);
    toastActions.showSuccessToast('Scoreboard refresh requested.');
  };
}
