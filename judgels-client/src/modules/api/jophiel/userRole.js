import { APP_CONFIG } from '../../../conf';
import { get, put } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/user-roles`;

export const userRoleAPI = {
  getUserRoles: token => {
    return get(baseURL, token);
  },

  setUserRoles: (token, usernameToRoleMap) => {
    return put(baseURL, token, usernameToRoleMap);
  },
};
