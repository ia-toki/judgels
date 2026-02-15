import { statsAPI } from '../../../modules/api/jerahmeel/stats';

export async function getTopUserStats(page, pageSize) {
  return await statsAPI.getTopUserStats(page, pageSize);
}
