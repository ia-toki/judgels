import { stringify } from 'query-string';

import { get } from '../http';

export const baseStatsURL = `/api/v2/stats`;

export const statsAPI = {
  getUserStats: username => {
    const params = stringify({ username });
    return get(`${baseStatsURL}/users/?${params}`);
  },

  getTopUserStats: (page, pageSize) => {
    const params = stringify({ page, pageSize });
    return get(`${baseStatsURL}/users/top?${params}`);
  },
};
