import { stringify } from 'query-string';

import { get, post } from '../http';

const baseURL = `/api/v2/admins`;

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
