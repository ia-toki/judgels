import { APP_CONFIG } from '../../../conf';
import { post } from '../../../modules/api/http';

export interface UserRegistrationData {
  username: string;
  password: string;
  email: string;
  name?: string;
  recaptchaResponse?: string;
}

export interface PasswordResetData {
  emailCode: string;
  newPassword: string;
}

const baseURL = `${APP_CONFIG.apiUrls.jophiel}/user-account`;

export const userAccountAPI = {
  registerUser: (userRegistrationData: UserRegistrationData): Promise<void> => {
    return post(`${baseURL}/register`, undefined, userRegistrationData);
  },

  activateUser: (emailCode: string): Promise<void> => {
    return post(`${baseURL}/activate/${emailCode}`);
  },

  requestToResetPassword: (email: string): Promise<void> => {
    return post(`${baseURL}/request-reset-password/${email}`);
  },

  resetPassword: (passwordResetData: PasswordResetData): Promise<void> => {
    return post(`${baseURL}/reset-password`, undefined, passwordResetData);
  },

  resendActivationEmail: (email: string): Promise<void> => {
    return post(`${baseURL}/resend-activation-email/${email}`);
  },
};
