import { queryOptions } from '@tanstack/react-query';

import { courseAPI } from '../api/jerahmeel/course';
import { courseChapterAPI } from '../api/jerahmeel/courseChapter';

export const courseBySlugQueryOptions = (token, courseSlug) =>
  queryOptions({
    queryKey: ['course-by-slug', courseSlug],
    queryFn: () => courseAPI.getCourseBySlug(token, courseSlug),
  });

export const courseChaptersQueryOptions = (token, courseJid) =>
  queryOptions({
    queryKey: ['course', courseJid, 'chapters'],
    queryFn: () => courseChapterAPI.getChapters(token, courseJid),
  });

export const courseChapterQueryOptions = (token, courseJid, chapterAlias) =>
  queryOptions({
    queryKey: ['course', courseJid, 'chapter', chapterAlias],
    queryFn: async () => courseChapterAPI.getChapter(token, courseJid, chapterAlias),
  });
