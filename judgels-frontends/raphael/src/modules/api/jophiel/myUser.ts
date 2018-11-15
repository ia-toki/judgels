import { APP_CONFIG } from 'conf';
import { get, post } from 'modules/api/http';

import { User } from './user';

export interface PasswordUpdateData {
  oldPassword: string;
  newPassword: string;
}

export function createMyUserAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.jophiel}/users/me`;

  return {
    getMyself: (token: string): Promise<User> => {
      return get(`${baseURL}`, token);
    },

    updateMyPassword: (token: string, data: PasswordUpdateData): Promise<void> => {
      return post(`${baseURL}/password`, token, data);
    },
  };
}
