import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, post } from '../http';

const baseURL = `${APP_CONFIG.apiUrls.uriel}/admins`;

export const urielAdminAPI = {
  getAdmins: (token, page) => {
    const params = stringify({ page });
    return get(`${baseURL}?${params}`, token);
  },

  upsertAdmins: (token, usernames) => {
    return post(`${baseURL}/batch-upsert`, token, usernames);
  },

  deleteAdmins: (token, usernames) => {
    return post(`${baseURL}/batch-delete`, token, usernames);
  },
};
