import { statsAPI } from '../../../../modules/api/jerahmeel/stats';
import { profileAPI } from '../../../../modules/api/jophiel/profile';

export async function getTopRatedProfiles(page, pageSize) {
  return await profileAPI.getTopRatedProfiles(page, pageSize);
}

export async function getTopUserStats(page, pageSize) {
  return await statsAPI.getTopUserStats(page, pageSize);
}
