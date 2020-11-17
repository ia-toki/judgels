import { profileAPI } from '../../../../modules/api/jophiel/profile';
import { userStatsAPI } from '../../../../modules/api/jerahmeel/user';

export function getTopRatedProfiles(page?: number, pageSize?: number) {
  return async () => {
    return await profileAPI.getTopRatedProfiles(page, pageSize);
  };
}

export function getTopUserStats(page?: number, pageSize?: number) {
  return async () => {
    return await userStatsAPI.getTopUserStats(page, pageSize);
  };
}
