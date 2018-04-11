import { APP_CONFIG } from '../../../conf';
import { delete_, get, postMultipart } from '../http';

export interface User {
  jid: string;
  username: string;
}

export interface UserWithAvatar extends User {
  avatarUrl?: string;
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

    updateUserAvatar: (token: string, userJid: string, file: File): Promise<void> => {
      return postMultipart(`${baseURL}/${userJid}/avatar`, token, file);
    },

    deleteUserAvatar: (token: string, userJid: string): Promise<void> => {
      return delete_(`${baseURL}/${userJid}/avatar`, token);
    },
  };
}
