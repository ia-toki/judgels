import { queryOptions } from '@tanstack/react-query';

import { adminCourseChapterAPI } from '../api/admin/courseChapter';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const courseChaptersQueryOptions = courseJid =>
  queryOptions({
    queryKey: ['admin', 'course', courseJid, 'chapters'],
    queryFn: () => adminCourseChapterAPI.getChapters(getToken(), courseJid),
  });

export const setCourseChaptersMutationOptions = courseJid => ({
  mutationFn: data => adminCourseChapterAPI.setChapters(getToken(), courseJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(courseChaptersQueryOptions(courseJid));
  },
});
