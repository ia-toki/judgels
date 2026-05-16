import { APP_CONFIG } from '../../../conf';
import { post } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/admin/user-rating`;

export const adminUserRatingAPI = {
  updateRatings: (token, data) => {
    return post(baseURL, token, data);
  },
};
