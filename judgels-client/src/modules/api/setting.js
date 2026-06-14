import { APP_CONFIG } from '../../conf';
import { get, put } from './http';

const baseURL = `${APP_CONFIG.apiUrl}/settings`;

export const settingAPI = {
  getSettings: token => {
    return get(baseURL, token);
  },

  updateAppSettings: (token, data) => {
    return put(`${baseURL}/app`, token, data);
  },
};
