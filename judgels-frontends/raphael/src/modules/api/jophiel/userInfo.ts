import { get, put } from 'modules/api/http';

import { baseUserURL } from './user';

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

const baseURL = (userJid: string) => `${baseUserURL(userJid)}/info`;

export const userInfoAPI = {
  getInfo: (token: string, userJid: string): Promise<UserInfo> => {
    return get(baseURL(userJid), token);
  },

  updateInfo: (token: string, userJid: string, userInfo: UserInfo): Promise<void> => {
    return put(baseURL(userJid), token, userInfo);
  },
};
