import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, post, postText } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/admin/users`;

export const adminUserAPI = {
  getUsers: (token, page, orderBy, orderDir) => {
    const params = stringify({ page, orderBy, orderDir });
    return get(`${baseURL}?${params}`, token);
  },

  getUserByUsername: (token, username) => {
    return get(`${baseURL}/username/${username}`, token);
  },

  createUser: (token, data) => {
    return post(baseURL, token, data);
  },

  upsertUsers: (token, csv) => {
    return postText(`${baseURL}/batch-upsert`, token, csv);
  },
};
