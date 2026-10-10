import { profileAPI } from '../../../../modules/api/profile';
import { trainingUserStatsAPI } from '../../../../modules/api/trainingUserStats';

export async function getTopRatedProfiles(page, pageSize) {
  return await profileAPI.getTopRatedProfiles(page, pageSize);
}

export async function getTopUserStats(page, pageSize) {
  return await trainingUserStatsAPI.getTopUserStats(page, pageSize);
}
