import { queryOptions } from '@tanstack/react-query';

import { courseAPI } from '../api/jerahmeel/course';
import { courseChapterAPI } from '../api/jerahmeel/courseChapter';
import { getToken } from '../session';

export const courseBySlugQueryOptions = courseSlug =>
  queryOptions({
    queryKey: ['course-by-slug', courseSlug],
    queryFn: () => courseAPI.getCourseBySlug(getToken(), courseSlug),
  });

export const courseChaptersQueryOptions = courseJid =>
  queryOptions({
    queryKey: ['course', courseJid, 'chapters'],
    queryFn: () => courseChapterAPI.getChapters(getToken(), courseJid),
  });

export const courseChapterQueryOptions = (courseJid, chapterAlias) =>
  queryOptions({
    queryKey: ['course', courseJid, 'chapter', chapterAlias],
    queryFn: () => courseChapterAPI.getChapter(getToken(), courseJid, chapterAlias),
  });
