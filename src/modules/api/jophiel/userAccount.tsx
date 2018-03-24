import { APP_CONFIG } from '../../../conf';
import { post } from '../http';

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

export function createUserAccountAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.jophiel}/users/account`;

  return {
    registerUser: (userRegistrationData: UserRegistrationData): Promise<void> => {
      return post(`${baseURL}/register`, undefined, userRegistrationData);
    },

    activateUser: (emailCode: string): Promise<void> => {
      return post(`${baseURL}/activate/${emailCode}`);
    },

    requestToResetUserPassword: (email: string): Promise<void> => {
      return post(`${baseURL}/request-reset-password/${email}`);
    },

    resetUserPassword: (passwordResetData: PasswordResetData): Promise<void> => {
      return post(`${baseURL}/reset-password`, undefined, passwordResetData);
    },
  };
}
