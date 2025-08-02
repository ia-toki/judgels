import { stringify } from 'query-string';

import { get } from '../http';

const baseURL = `/api/v2/profiles`;

export const profileAPI = {
  getTopRatedProfiles: (page, pageSize) => {
    const params = stringify({ page, pageSize });
    return get(`${baseURL}/top/?${params}`);
  },

  getBasicProfile: userJid => {
    return get(`${baseURL}/${userJid}/basic`);
  },
};
