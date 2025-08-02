import { stringify } from 'query-string';

import { get } from '../http';

const baseURL = `/api/v2/contest-history`;

export const contestHistoryAPI = {
  getPublicHistory: username => {
    const params = stringify({ username });
    return get(`${baseURL}/public?${params}`);
  },
};
