import { APP_CONFIG } from '../../../conf';
import { get, put } from '../http';

export interface UserInfo {
  name?: string;
  gender?: string;
  nationality?: string;
  homeAddress?: string;
  institution?: string;
  country?: string;
  province?: string;
  city?: string;
  shirtSize?: string;
}

export const userInfoGender = {
  ['MALE']: 'Male',
  ['FEMALE']: 'Female',
};

export function createUserInfoAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.jophiel}/users`;

  return {
    getInfo: (token: string, userJid: string): Promise<UserInfo> => {
      return get(`${baseURL}/${userJid}/info`, token);
    },

    updateInfo: (token: string, userJid: string, userInfo: UserInfo): Promise<void> => {
      return put(`${baseURL}/${userJid}/info`, token, userInfo);
    },
  };
}
