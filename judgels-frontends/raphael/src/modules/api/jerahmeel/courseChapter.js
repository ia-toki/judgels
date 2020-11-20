import { get, put } from '../http';

import { baseCourseURL } from './course';

const baseURL = courseJid => `${baseCourseURL(courseJid)}/chapters`;

export const courseChapterAPI = {
  getChapters: (token, courseJid) => {
    return get(baseURL(courseJid), token);
  },

  setChapters: (token, courseJid, data) => {
    return put(baseURL(courseJid), token, data);
  },

  getChapter: (token, courseJid, chapterAlias) => {
    return get(`${baseURL(courseJid)}/${chapterAlias}`, token);
  },
};
