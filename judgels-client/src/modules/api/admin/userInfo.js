import { APP_CONFIG } from '../../../conf';
import { get, put } from '../http';

const baseURL = userJid => `${APP_CONFIG.apiUrl}/admin/users/${userJid}/info`;

export const adminUserInfoAPI = {
  getInfo: (token, userJid) => {
    return get(baseURL(userJid), token);
  },

  updateInfo: (token, userJid, userInfo) => {
    return put(baseURL(userJid), token, userInfo);
  },
};
