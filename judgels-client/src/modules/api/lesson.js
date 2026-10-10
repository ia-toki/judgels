import { stringify } from 'query-string';

import { APP_CONFIG } from '../../conf';
import { get, post } from './http';

export const LessonErrors = {
  SlugAlreadyExists: 'LessonSlugAlreadyExists',
};

export function getLessonName(lesson, language) {
  return (language && lesson.titlesByLanguage[language]) || lesson.titlesByLanguage[lesson.defaultLanguage];
}

export function constructLessonName(title, alias) {
  return (alias && alias + '. ') + (title || '');
}

export const baseLessonsURL = `${APP_CONFIG.apiUrl}/v4/lessons`;

export function baseLessonURL(lessonJid) {
  return `${baseLessonsURL}/${lessonJid}`;
}

export const lessonAPI = {
  createLesson: (token, data) => {
    return post(baseLessonsURL, token, data);
  },

  getLesson: (token, lessonJid) => {
    return get(baseLessonURL(lessonJid), token);
  },

  updateLesson: (token, lessonJid, data) => {
    return post(baseLessonURL(lessonJid), token, data);
  },

  getLessons: (token, term, page) => {
    const params = stringify({ term, page });
    return get(`${baseLessonsURL}?${params}`, token);
  },
};
