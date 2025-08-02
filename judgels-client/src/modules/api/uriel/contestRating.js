import { stringify } from 'query-string';

import { get } from '../http';

const baseURL = `/api/v2/contest-rating`;

export const contestRatingAPI = {
  getContestsPendingRating: token => {
    return get(`${baseURL}/pending`, token);
  },

  getRatingHistory: username => {
    const params = stringify({ username });
    return get(`${baseURL}/history?${params}`);
  },
};
