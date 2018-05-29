import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

export interface User {
  jid: string;
  username: string;
}

export interface UserInfo {
  username: string;
}

export interface UsersMap {
  [userJid: string]: UserInfo;
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
  };
}
