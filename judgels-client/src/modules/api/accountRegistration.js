import { APP_CONFIG } from '../../conf';
import { post } from './http';

const baseURL = `${APP_CONFIG.apiUrl}/v4/account/registration`;

export const accountRegistrationAPI = {
  registerUser: data => {
    return post(baseURL, undefined, data);
  },

  registerGoogleUser: data => {
    return post(`${baseURL}/google`, undefined, data);
  },

  activateUser: emailCode => {
    return post(`${baseURL}/activate/${emailCode}`);
  },

  resendActivationEmail: email => {
    return post(`${baseURL}/resend-activation-email/${email}`);
  },
};
