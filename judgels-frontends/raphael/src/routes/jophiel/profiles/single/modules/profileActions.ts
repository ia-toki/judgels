import { selectToken } from '../../../../../modules/session/sessionSelectors';
import { profileAPI } from '../../../../../modules/api/jophiel/profile';
import { contestHistoryAPI } from '../../../../../modules/api/uriel/contestHistory';
import { userStatsAPI } from '../../../../../modules/api/jerahmeel/user';
import { submissionProgrammingAPI } from '../../../../../modules/api/jerahmeel/submissionProgramming';

export function getBasicProfile(userJid: string) {
  return async () => {
    return await profileAPI.getBasicProfile(userJid);
  };
}

export function getUserStats(username: string) {
  return async () => {
    return await userStatsAPI.getUserStats(username);
  };
}

export function getContestPublicHistory(username: string) {
  return async () => {
    return await contestHistoryAPI.getPublicHistory(username);
  };
}

export function getSubmissions(userJid: string, page?: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await submissionProgrammingAPI.getSubmissions(token, undefined, userJid, undefined, page);
  };
}
