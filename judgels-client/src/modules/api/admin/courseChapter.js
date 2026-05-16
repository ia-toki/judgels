import { APP_CONFIG } from '../../../conf';
import { get, put } from '../http';

const baseURL = courseJid => `${APP_CONFIG.apiUrl}/admin/courses/${courseJid}/chapters`;

export const adminCourseChapterAPI = {
  getChapters: (token, courseJid) => {
    return get(baseURL(courseJid), token);
  },

  setChapters: (token, courseJid, data) => {
    return put(baseURL(courseJid), token, data);
  },
};
