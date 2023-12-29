import { get, put } from '../http';
import { baseUserURL } from './user';

export const userInfoGender = {
  MALE: 'Male',
  FEMALE: 'Female',
};

const baseURL = userJid => `${baseUserURL(userJid)}/info`;

export const userInfoAPI = {
  getInfo: (token, userJid) => {
    return get(baseURL(userJid), token);
  },

  updateInfo: (token, userJid, userInfo) => {
    return put(baseURL(userJid), token, userInfo);
  },
};
