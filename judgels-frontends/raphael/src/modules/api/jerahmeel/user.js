import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

export const baseUserStatsURL = `${APP_CONFIG.apiUrls.jerahmeel}/user-stats`;

export const userStatsAPI = {
  getUserStats: username => {
    const params = stringify({ username });
    return get(`${baseUserStatsURL}?${params}`);
  },

  getTopUserStats: (page, pageSize) => {
    const params = stringify({ page, pageSize });
    return get(`${baseUserStatsURL}/top?${params}`);
  },
};
