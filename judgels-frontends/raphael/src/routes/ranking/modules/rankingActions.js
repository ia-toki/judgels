import { userStatsAPI } from '../../../modules/api/jerahmeel/user';

export function getTopUserStats(page, pageSize) {
  return async () => {
    return await userStatsAPI.getTopUserStats(page, pageSize);
  };
}
