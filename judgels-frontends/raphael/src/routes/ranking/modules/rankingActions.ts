import { userStatsAPI } from '../../../modules/api/jerahmeel/user';

export function getTopUserStats(page?: number, pageSize?: number) {
  return async () => {
    return await userStatsAPI.getTopUserStats(page, pageSize);
  };
}
