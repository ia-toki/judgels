import { delete_, get, postMultipart } from '../http';

import { baseUserURL } from './user';

const baseURL = userJid => `${baseUserURL(userJid)}/avatar`;

export const userAvatarAPI = {
  deleteAvatar: (token, userJid) => {
    return delete_(`${baseURL(userJid)}`, token);
  },

  avatarExists: userJid => {
    return get(`${baseURL(userJid)}/exists`);
  },

  renderAvatar: userJid => Promise.resolve(`${baseURL(userJid)}`),

  updateAvatar: (token, userJid, file) => {
    return postMultipart(`${baseURL(userJid)}`, token, { file: file });
  },
};
