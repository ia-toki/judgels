import { statsAPI } from '../../../../../modules/api/jerahmeel/stats';
import { submissionProgrammingAPI } from '../../../../../modules/api/jerahmeel/submissionProgramming';
import { profileAPI } from '../../../../../modules/api/jophiel/profile';
import { contestHistoryAPI } from '../../../../../modules/api/uriel/contestHistory';
import { getToken } from '../../../../../modules/session';

export async function getBasicProfile(userJid) {
  return await profileAPI.getBasicProfile(userJid);
}

export async function getUserStats(username) {
  return await statsAPI.getUserStats(username);
}

export async function getContestPublicHistory(username) {
  return await contestHistoryAPI.getPublicHistory(username);
}

export async function getSubmissions(username, page) {
  const token = getToken();
  return await submissionProgrammingAPI.getSubmissions(token, undefined, username, undefined, undefined, page);
}
