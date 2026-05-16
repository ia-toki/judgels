import { queryOptions } from '@tanstack/react-query';

import { adminChapterLessonAPI } from '../api/admin/chapterLesson';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const chapterLessonsQueryOptions = chapterJid =>
  queryOptions({
    queryKey: ['admin', 'chapter', chapterJid, 'lessons'],
    queryFn: () => adminChapterLessonAPI.getLessons(getToken(), chapterJid),
  });

export const setChapterLessonsMutationOptions = chapterJid => ({
  mutationFn: data => adminChapterLessonAPI.setLessons(getToken(), chapterJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(chapterLessonsQueryOptions(chapterJid));
  },
});
