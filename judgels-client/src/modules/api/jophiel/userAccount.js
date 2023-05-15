import { APP_CONFIG } from '../../../conf';
import { post } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/user-account`;

export const userAccountAPI = {
  registerUser: data => {
    return post(`${baseURL}/register`, undefined, data);
  },

  registerGoogleUser: data => {
    return post(`${baseURL}/register-google`, undefined, data);
  },

  activateUser: emailCode => {
    return post(`${baseURL}/activate/${emailCode}`);
  },

  requestToResetPassword: email => {
    return post(`${baseURL}/request-reset-password/${email}`);
  },

  resetPassword: data => {
    return post(`${baseURL}/reset-password`, undefined, data);
  },

  resendActivationEmail: email => {
    return post(`${baseURL}/resend-activation-email/${email}`);
  },
};
