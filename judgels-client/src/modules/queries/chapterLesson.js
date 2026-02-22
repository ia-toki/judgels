import { queryOptions } from '@tanstack/react-query';

import { chapterLessonAPI } from '../api/jerahmeel/chapterLesson';
import { getToken } from '../session';

export const chapterLessonsQueryOptions = chapterJid =>
  queryOptions({
    queryKey: ['chapter', chapterJid, 'lessons'],
    queryFn: () => chapterLessonAPI.getLessons(getToken(), chapterJid),
  });

export const chapterLessonStatementQueryOptions = (chapterJid, lessonAlias, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['chapter', chapterJid, 'lesson', lessonAlias, 'statement', ...(params ? [params] : [])],
    queryFn: () => chapterLessonAPI.getLessonStatement(getToken(), chapterJid, lessonAlias, language),
  });
};
