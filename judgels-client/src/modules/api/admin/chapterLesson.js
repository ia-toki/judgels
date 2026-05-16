import { APP_CONFIG } from '../../../conf';
import { get, put } from '../http';

const baseURL = chapterJid => `${APP_CONFIG.apiUrl}/admin/chapters/${chapterJid}/lessons`;

export const adminChapterLessonAPI = {
  getLessons: (token, chapterJid) => {
    return get(baseURL(chapterJid), token);
  },

  setLessons: (token, chapterJid, data) => {
    return put(baseURL(chapterJid), token, data);
  },
};
