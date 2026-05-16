import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/admin/contest-rating`;

export const adminContestRatingAPI = {
  getContestsPendingRating: token => {
    return get(`${baseURL}/pending`, token);
  },
};
