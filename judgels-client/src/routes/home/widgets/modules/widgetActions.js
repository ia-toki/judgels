import { profileAPI } from '../../../../modules/api/profile';
import { trainingStatsAPI } from '../../../../modules/api/trainingStats';

export async function getTopRatedProfiles(page, pageSize) {
  return await profileAPI.getTopRatedProfiles(page, pageSize);
}

export async function getTopUserStats(page, pageSize) {
  return await trainingStatsAPI.getTopUserStats(page, pageSize);
}
