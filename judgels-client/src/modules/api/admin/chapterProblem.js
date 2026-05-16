import { APP_CONFIG } from '../../../conf';
import { get, put } from '../http';

const baseURL = chapterJid => `${APP_CONFIG.apiUrl}/admin/chapters/${chapterJid}/problems`;

export const adminChapterProblemAPI = {
  getProblems: (token, chapterJid) => {
    return get(baseURL(chapterJid), token);
  },

  setProblems: (token, chapterJid, data) => {
    return put(baseURL(chapterJid), token, data);
  },
};
