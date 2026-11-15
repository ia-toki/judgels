import { APP_CONFIG } from '../../conf';
import { get } from './http';

const baseURL = `${APP_CONFIG.apiUrl}/v2/contest-rating`;

export const contestRatingAPI = {
  getContestsPendingRating: token => {
    return get(`${baseURL}/pending`, token);
  },
};
