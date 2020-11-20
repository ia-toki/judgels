import { stringify } from 'query-string';

import { get, put } from '../http';
import { baseChapterURL } from './chapter';

const baseURL = chapterJid => `${baseChapterURL(chapterJid)}/lessons`;

export const chapterLessonAPI = {
  setLessons: (token, chapterJid, data) => {
    return put(baseURL(chapterJid), token, data);
  },

  getLessons: (token, chapterJid) => {
    return get(baseURL(chapterJid), token);
  },

  getLessonStatement: (token, chapterJid, lessonAlias, language) => {
    const params = stringify({ language });
    return get(`${baseURL(chapterJid)}/${lessonAlias}/statement?${params}`, token);
  },
};
