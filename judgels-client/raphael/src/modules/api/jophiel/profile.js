import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, post } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/profiles`;

export const profileAPI = {
  getProfiles: userJids => {
    return post(`${baseURL}`, undefined, userJids);
  },

  getTopRatedProfiles: (page, pageSize) => {
    const params = stringify({ page, pageSize });
    return get(`${baseURL}/top/?${params}`);
  },

  getBasicProfile: userJid => {
    return get(`${baseURL}/${userJid}/basic`);
  },
};
