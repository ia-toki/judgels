import { get, post } from '../../../modules/api/http';

import { baseUserURL, User } from './user';

export interface PasswordUpdateData {
  oldPassword: string;
  newPassword: string;
}

const baseURL = baseUserURL('me');

export const myUserAPI = {
  getMyself: (token: string): Promise<User> => {
    return get(`${baseURL}`, token);
  },

  updateMyPassword: (token: string, data: PasswordUpdateData): Promise<void> => {
    return post(`${baseURL}/password`, token, data);
  },
};
