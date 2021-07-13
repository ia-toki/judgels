import { statsAPI } from '../../../modules/api/jerahmeel/stats';

export function getTopUserStats(page, pageSize) {
  return async () => {
    return await statsAPI.getTopUserStats(page, pageSize);
  };
}
