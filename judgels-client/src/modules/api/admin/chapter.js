import { APP_CONFIG } from '../../../conf';
import { get, post } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/admin/chapters`;

export const adminChapterAPI = {
  getChapters: token => {
    return get(baseURL, token);
  },

  createChapter: (token, data) => {
    return post(baseURL, token, data);
  },

  updateChapter: (token, chapterJid, data) => {
    return post(`${baseURL}/${chapterJid}`, token, data);
  },
};
