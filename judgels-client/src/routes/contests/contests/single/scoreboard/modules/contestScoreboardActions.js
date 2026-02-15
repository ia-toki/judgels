import { NotFoundError } from '../../../../../../modules/api/error';
import { contestScoreboardAPI } from '../../../../../../modules/api/uriel/contestScoreboard';
import { contestSubmissionProgrammingAPI } from '../../../../../../modules/api/uriel/contestSubmissionProgramming';
import { getToken } from '../../../../../../modules/session';
import { getWebPrefs } from '../../../../../../modules/webPrefs';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export async function getScoreboard(contestJid, frozen, showClosedProblems, page) {
  const token = getToken();
  return await contestScoreboardAPI.getScoreboard(token, contestJid, frozen, showClosedProblems, page);
}

export async function refreshScoreboard(contestJid) {
  const token = getToken();
  await contestScoreboardAPI.refreshScoreboard(token, contestJid);
  toastActions.showSuccessToast('Scoreboard refresh requested.');
}

export async function getSubmissionSourceImage(contestJid, userJid, problemJid) {
  const { isDarkMode } = getWebPrefs();
  return await contestSubmissionProgrammingAPI.getSubmissionSourceImage(contestJid, userJid, problemJid, isDarkMode);
}

export async function getSubmissionInfo(contestJid, userJid, problemJid) {
  return await contestSubmissionProgrammingAPI.getSubmissionInfo(contestJid, userJid, problemJid);
}

export async function getUserProblemSubmissions(contestJid, userJid, problemJid) {
  const token = getToken();
  return await contestSubmissionProgrammingAPI.getUserProblemSubmissions(token, contestJid, userJid, problemJid);
}

export async function getSubmissionWithSource(contestJid, submissionId, language) {
  const token = getToken();
  const submissionWithSource = await contestSubmissionProgrammingAPI.getSubmissionWithSource(
    token,
    submissionId,
    language
  );
  if (contestJid !== submissionWithSource.data.submission.containerJid) {
    throw new NotFoundError();
  }
  return submissionWithSource;
}
