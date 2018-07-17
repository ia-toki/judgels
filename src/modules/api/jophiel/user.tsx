import { APP_CONFIG } from '../../../conf';
import { delete_, get, post, postMultipart, put } from '../http';

export interface User {
  jid: string;
  username: string;
  avatarUrl?: string;
}

export interface UserProfile {
  name?: string;
  gender?: string;
  nationality?: string;
  homeAddress?: string;
  institution?: string;
  country?: string;
  province?: string;
  city?: string;
  shirtSize?: string;
}

export const userProfileGender = {
  ['MALE']: 'Male',
  ['FEMALE']: 'Female',
};

export interface UserRegistrationData {
  username: string;
  password: string;
  email: string;
  name?: string;
  recaptchaResponse?: string;
}

export interface PasswordUpdateData {
  oldPassword: string;
  newPassword: string;
}

export interface PasswordResetData {
  emailCode: string;
  newPassword: string;
}

export function createUserAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.jophiel}/users`;

  return {
    usernameExists: (username: string): Promise<boolean> => {
      return get(`${baseURL}/username/${username}/exists`);
    },

    emailExists: (email: string): Promise<boolean> => {
      return get(`${baseURL}/email/${email}/exists`);
    },

    getMyself: (token: string): Promise<User> => {
      return get(`${baseURL}/me`, token);
    },

    updateMyPassword: (token: string, passwordUpdateData: PasswordUpdateData): Promise<void> => {
      return post(`${baseURL}/me/password`, token, passwordUpdateData);
    },

    registerUser: (userRegistrationData: UserRegistrationData): Promise<void> => {
      return post(`${baseURL}/register`, undefined, userRegistrationData);
    },

    activateUser: (emailCode: string): Promise<void> => {
      return post(`${baseURL}/activate/${emailCode}`);
    },

    getUserProfile: (token: string, userJid: string): Promise<UserProfile> => {
      return get(`${baseURL}/${userJid}/profile`, token);
    },

    updateUserProfile: (token: string, userJid: string, userProfile: UserProfile): Promise<void> => {
      return put(`${baseURL}/${userJid}/profile`, token, userProfile);
    },

    requestToResetUserPassword: (email: string): Promise<void> => {
      return post(`${baseURL}/request-reset-password/${email}`);
    },

    resetUserPassword: (passwordResetData: PasswordResetData): Promise<void> => {
      return post(`${baseURL}/reset-password`, undefined, passwordResetData);
    },

    updateUserAvatar: (token: string, userJid: string, file: File): Promise<UserProfile> => {
      return postMultipart(`${baseURL}/${userJid}/avatar`, token, file);
    },

    deleteUserAvatar: (token: string, userJid: string): Promise<void> => {
      return delete_(`${baseURL}/${userJid}/avatar`, token);
    },
  };
}
