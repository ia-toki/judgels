import { APP_CONFIG } from '../../conf';
import { post } from './http';

export const AccountRegistrationErrors = {
  UsernameAlreadyExists: 'AccountRegistrationUsernameAlreadyExists',
  EmailAlreadyExists: 'AccountRegistrationEmailAlreadyExists',
};

const baseURL = `${APP_CONFIG.apiUrl}/v4/account`;

export const accountAPI = {
  registerUser: data => {
    return post(`${baseURL}/register`, undefined, data);
  },

  registerGoogleUser: data => {
    return post(`${baseURL}/register-google`, undefined, data);
  },

  activateUser: emailCode => {
    return post(`${baseURL}/activate/${emailCode}`);
  },

  resendActivationEmail: email => {
    return post(`${baseURL}/resend-activation-email/${email}`);
  },

  requestResetPassword: email => {
    return post(`${baseURL}/request-reset-password/${email}`);
  },

  resetPassword: data => {
    return post(`${baseURL}/reset-password`, undefined, data);
  },
};
