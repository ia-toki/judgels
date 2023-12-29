import { statsAPI } from '../../../../../modules/api/jerahmeel/stats';
import { submissionProgrammingAPI } from '../../../../../modules/api/jerahmeel/submissionProgramming';
import { profileAPI } from '../../../../../modules/api/jophiel/profile';
import { contestHistoryAPI } from '../../../../../modules/api/uriel/contestHistory';
import { selectToken } from '../../../../../modules/session/sessionSelectors';

export function getBasicProfile(userJid) {
  return async () => {
    return await profileAPI.getBasicProfile(userJid);
  };
}

export function getUserStats(username) {
  return async () => {
    return await statsAPI.getUserStats(username);
  };
}

export function getContestPublicHistory(username) {
  return async () => {
    return await contestHistoryAPI.getPublicHistory(username);
  };
}

export function getSubmissions(username, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await submissionProgrammingAPI.getSubmissions(token, undefined, username, undefined, undefined, page);
  };
}
