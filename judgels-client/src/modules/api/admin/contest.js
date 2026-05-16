import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { BadRequestError } from '../error';
import { get, post } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/admin/contests`;

export const adminContestAPI = {
  getContests: (token, name, page) => {
    const params = stringify({ name, page });
    return get(`${baseURL}?${params}`, token);
  },

  createContest: (token, data) => {
    return post(baseURL, token, data).catch(error => {
      throw error instanceof BadRequestError ? error : error;
    });
  },

  updateContest: (token, contestJid, data) => {
    return post(`${baseURL}/${contestJid}`, token, data);
  },
};
