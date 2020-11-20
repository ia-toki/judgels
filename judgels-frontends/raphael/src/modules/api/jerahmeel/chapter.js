import { get, post } from '../http';
import { APP_CONFIG } from '../../../conf';

export const baseChaptersURL = `${APP_CONFIG.apiUrls.jerahmeel}/chapters`;

export function baseChapterURL(chapterJid) {
  return `${baseChaptersURL}/${chapterJid}`;
}

export const chapterAPI = {
  createChapter: (token, data) => {
    return post(baseChaptersURL, token, data);
  },

  updateChapter: (token, chapterJid, data) => {
    return post(`${baseChapterURL(chapterJid)}`, token, data);
  },

  getChapters: token => {
    return get(baseChaptersURL, token);
  },
};
