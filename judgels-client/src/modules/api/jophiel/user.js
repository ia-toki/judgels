import { stringify } from 'query-string';

import { get } from '../http';

export const baseUsersURL = `/api/v2/users`;

export function baseUserURL(userJid) {
  return `${baseUsersURL}/${userJid}`;
}

export const userAPI = {
  getUser: (token, userJid) => {
    return get(`${baseUsersURL}/${userJid}`, token);
  },

  getMyself: token => {
    return get(`${baseUsersURL}/me`, token);
  },

  getUsers: (token, page, orderBy, orderDir) => {
    const params = stringify({ page, orderBy, orderDir });
    return get(`${baseUsersURL}?${params}`, token);
  },
};
