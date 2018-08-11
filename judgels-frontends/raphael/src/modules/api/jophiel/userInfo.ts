import { APP_CONFIG } from 'conf';
import { get, put } from 'modules/api/http';

export interface UserInfo {
  name?: string;
  gender?: string;
  country?: string;
  homeAddress?: string;
  institutionName?: string;
  institutionCountry?: string;
  institutionProvince?: string;
  institutionCity?: string;
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
