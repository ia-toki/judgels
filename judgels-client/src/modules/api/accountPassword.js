import { APP_CONFIG } from '../../conf';
import { post } from './http';

const baseURL = `${APP_CONFIG.apiUrl}/v4/account/password`;

export const accountPasswordAPI = {
  requestToResetPassword: email => {
    return post(`${baseURL}/request-reset/${email}`);
  },

  resetPassword: data => {
    return post(`${baseURL}/reset`, undefined, data);
  },
};
