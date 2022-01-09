import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

const baseURL = `${APP_CONFIG.apiUrls.uriel}/contest-rating`;

export const contestRatingAPI = {
  getContestsPendingRating: token => {
    return get(`${baseURL}/pending`, token);
  },

  getRatingHistory: username => {
    const params = stringify({ username });
    return get(`${baseURL}/history?${params}`);
  },
};
