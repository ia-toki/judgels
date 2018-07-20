import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, post } from '../http';
import { Page, OrderDir } from '../pagination';

export interface User {
  jid: string;
  username: string;
}

export interface UserInfo {
  username: string;
  rating?: number;
}

export interface UsersMap {
  [userJid: string]: UserInfo;
}

export interface UsernamesMap {
  [username: string]: User;
}

export function createUserAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.jophiel}/users`;

  return {
    usernameExists: (username: string): Promise<boolean> => {
      return get(`${baseURL}/username/${username}/exists`);
    },

    emailExists: (email: string): Promise<boolean> => {
      return get(`${baseURL}/email/${email}/exists`);
    },

    getUsersByUsernames: (usernames: string[]): Promise<UsernamesMap> => {
      return post(`${baseURL}/usernames`, undefined, usernames);
    },

    getUsers: (token: string, page: number, orderBy?: string, orderDir?: OrderDir): Promise<Page<User>> => {
      const params = stringify({ page, orderBy, orderDir });
      return get(`${baseURL}/?${params}`, token);
    },
  };
}
