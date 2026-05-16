import { profileAPI } from '../../../../modules/api/profile';
import { statsAPI } from '../../../../modules/api/stats';

export async function getTopRatedProfiles(page, pageSize) {
  return await profileAPI.getTopRatedProfiles(page, pageSize);
}

export async function getTopUserStats(page, pageSize) {
  return await statsAPI.getTopUserStats(page, pageSize);
}
