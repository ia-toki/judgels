import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/contest-history`;

export const contestHistoryAPI = {
  getPublicHistory: username => {
    const params = stringify({ username });
    return get(`${baseURL}/public?${params}`);
  },
};
