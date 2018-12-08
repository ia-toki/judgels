import { stringify } from 'query-string';

import { APP_CONFIG } from 'conf';
import { get } from 'modules/api/http';
import { Page, OrderDir } from 'modules/api/pagination';

export interface User {
  jid: string;
  username: string;
}

export interface UsernamesMap {
  [username: string]: string;
}

export const baseUsersURL = `${APP_CONFIG.apiUrls.jophiel}/users`;

export function baseUserURL(userJid: string) {
  return `${baseUsersURL}/${userJid}`;
}

export const userAPI = {
  getUsers: (token: string, page?: number, orderBy?: string, orderDir?: OrderDir): Promise<Page<User>> => {
    const params = stringify({ page, orderBy, orderDir });
    return get(`${baseUsersURL}/?${params}`, token);
  },
};
