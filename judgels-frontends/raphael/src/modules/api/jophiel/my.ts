import { APP_CONFIG } from 'conf';
import { get, post } from 'modules/api/http';

import { User } from './user';
import { JophielRole } from './role';

export interface PasswordUpdateData {
  oldPassword: string;
  newPassword: string;
}

export function createMyAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.jophiel}/users/me`;

  return {
    getMyself: (token: string): Promise<User> => {
      return get(`${baseURL}`, token);
    },

    updateMyPassword: (token: string, passwordUpdateData: PasswordUpdateData): Promise<void> => {
      return post(`${baseURL}/password`, token, passwordUpdateData);
    },

    getMyRole: (token: string): Promise<JophielRole> => {
      return get(`${baseURL}/role`, token);
    },
  };
}
