import { APP_CONFIG } from 'conf';
import { delete_, get, postMultipart } from 'modules/api/http';

export function createUserAvatarAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.jophiel}/users`;

  return {
    deleteAvatar: (token: string, userJid: string): Promise<void> => {
      return delete_(`${baseURL}/${userJid}/avatar`, token);
    },

    avatarExists: (userJid: string): Promise<boolean> => {
      return get(`${baseURL}/${userJid}/avatar/exists`);
    },

    renderAvatar: (userJid: string) => Promise.resolve(`${baseURL}/${userJid}/avatar`),

    updateAvatar: (token: string, userJid: string, file: File): Promise<void> => {
      return postMultipart(`${baseURL}/${userJid}/avatar`, token, { file: file });
    },
  };
}
