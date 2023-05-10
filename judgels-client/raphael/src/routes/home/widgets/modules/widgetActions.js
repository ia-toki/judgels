import { profileAPI } from '../../../../modules/api/jophiel/profile';
import { statsAPI } from '../../../../modules/api/jerahmeel/stats';

export function getTopRatedProfiles(page, pageSize) {
  return async () => {
    return await profileAPI.getTopRatedProfiles(page, pageSize);
  };
}

export function getTopUserStats(page, pageSize) {
  return async () => {
    return await statsAPI.getTopUserStats(page, pageSize);
  };
}
