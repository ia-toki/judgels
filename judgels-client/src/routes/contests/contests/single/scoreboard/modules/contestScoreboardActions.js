import { NotFoundError } from '../../../../../../modules/api/error';
import { contestScoreboardAPI } from '../../../../../../modules/api/uriel/contestScoreboard';
import { contestSubmissionProgrammingAPI } from '../../../../../../modules/api/uriel/contestSubmissionProgramming';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { getWebPrefs } from '../../../../../../modules/webPrefs';

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
  return async (dispatch, getState) => {
    const { isDarkMode } = getWebPrefs();
    return await contestSubmissionProgrammingAPI.getSubmissionSourceImage(contestJid, userJid, problemJid, isDarkMode);
  };
}

export function getSubmissionInfo(contestJid, userJid, problemJid) {
  return async () => {
    return await contestSubmissionProgrammingAPI.getSubmissionInfo(contestJid, userJid, problemJid);
  };
}

export function getUserProblemSubmissions(contestJid, userJid, problemJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestSubmissionProgrammingAPI.getUserProblemSubmissions(token, contestJid, userJid, problemJid);
  };
}

export function getSubmissionWithSource(contestJid, submissionId, language) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const submissionWithSource = await contestSubmissionProgrammingAPI.getSubmissionWithSource(
      token,
      submissionId,
      language
    );
    if (contestJid !== submissionWithSource.data.submission.containerJid) {
      throw new NotFoundError();
    }
    return submissionWithSource;
  };
}
