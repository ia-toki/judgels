import { stringify } from 'query-string';

import { APP_CONFIG } from '../../conf';
import { get } from './http';

export function getLessonName(lesson, language) {
  return (language && lesson.titlesByLanguage[language]) || lesson.titlesByLanguage[lesson.defaultLanguage];
}

export function constructLessonName(title, alias) {
  return (alias && alias + '. ') + (title || '');
}

export const baseLessonsURL = `${APP_CONFIG.apiUrl}/v4/lessons`;

export const lessonAPI = {
  getLessons: (token, term, page) => {
    const params = stringify({ term, page });
    return get(`${baseLessonsURL}?${params}`, token);
  },
};
