import { APP_CONFIG } from '../../conf';
import { get, post, put } from './http';

const baseURL = `${APP_CONFIG.apiUrl}/v2/user-roles`;

export const userRoleAPI = {
  getRoles: token => {
    return get(baseURL, token);
  },

  upsertRoles: (token, usernames, role) => {
    return post(`${baseURL}/batch-upsert`, token, { usernames, role });
  },

  setRole: (token, userJid, role) => {
    return put(`${baseURL}/${userJid}`, token, role);
  },
};
