import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { contestScoreboardAPI } from '../../../../../../modules/api/uriel/contestScoreboard';
import { contestSubmissionProgrammingAPI } from '../../../../../../modules/api/uriel/contestSubmissionProgramming';
import * as toastActions from '../../../../../../modules/toast/toastActions';

export function getScoreboard(contestJid, frozen, showClosedProblems, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestScoreboardAPI.getScoreboard(token, contestJid, frozen, showClosedProblems, page);
  };
}

export function refreshScoreboard(contestJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestScoreboardAPI.refreshScoreboard(token, contestJid);
    toastActions.showSuccessToast('Scoreboard refresh requested.');
  };
}

export function getSubmissionSourceImage(contestJid, userJid, problemJid) {
  return async () => {
    return await contestSubmissionProgrammingAPI.getSubmissionSourceImage(contestJid, userJid, problemJid);
  };
}

export function getSubmissionInfo(contestJid, userJid, problemJid) {
  return async () => {
    return await contestSubmissionProgrammingAPI.getSubmissionInfo(contestJid, userJid, problemJid);
  };
}
