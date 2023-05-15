import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

export const baseStatsURL = `${APP_CONFIG.apiUrl}/stats`;

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
