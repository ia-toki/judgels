import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

export const baseUsersURL = `${APP_CONFIG.apiUrl}/users`;

export function baseUserURL(userJid) {
  return `${baseUsersURL}/${userJid}`;
}

export const userAPI = {
  getUser: (token, userJid) => {
    return get(`${baseUsersURL}/${userJid}`, token);
  },

  getUsers: (token, page, orderBy, orderDir) => {
    const params = stringify({ page, orderBy, orderDir });
    return get(`${baseUsersURL}?${params}`, token);
  },
};
