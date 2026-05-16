import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, post } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/admin/problemsets`;

export const adminProblemSetAPI = {
  getProblemSets: (token, archiveSlug, name, page) => {
    const params = stringify({ archiveSlug, name, page });
    return get(`${baseURL}?${params}`, token);
  },

  getProblemSetBySlug: (token, slug) => {
    return get(`${baseURL}/slug/${slug}`, token);
  },

  createProblemSet: (token, data) => {
    return post(baseURL, token, data);
  },

  updateProblemSet: (token, problemSetJid, data) => {
    return post(`${baseURL}/${problemSetJid}`, token, data);
  },
};
