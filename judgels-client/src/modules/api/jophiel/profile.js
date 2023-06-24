import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/profiles`;

export const profileAPI = {
  getTopRatedProfiles: (page, pageSize) => {
    const params = stringify({ page, pageSize });
    return get(`${baseURL}/top/?${params}`);
  },

  getBasicProfile: userJid => {
    return get(`${baseURL}/${userJid}/basic`);
  },
};
