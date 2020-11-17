import { delete_, get, postMultipart } from '../../../modules/api/http';

import { baseUserURL } from './user';

const baseURL = (userJid: string) => `${baseUserURL(userJid)}/avatar`;

export const userAvatarAPI = {
  deleteAvatar: (token: string, userJid: string): Promise<void> => {
    return delete_(`${baseURL(userJid)}`, token);
  },

  avatarExists: (userJid: string): Promise<boolean> => {
    return get(`${baseURL(userJid)}/exists`);
  },

  renderAvatar: (userJid: string) => Promise.resolve(`${baseURL(userJid)}`),

  updateAvatar: (token: string, userJid: string, file: File): Promise<void> => {
    return postMultipart(`${baseURL(userJid)}`, token, { file: file });
  },
};
