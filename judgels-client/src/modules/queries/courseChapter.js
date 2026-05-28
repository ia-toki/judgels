import { queryOptions } from '@tanstack/react-query';

import { courseChapterAPI } from '../api/courseChapter';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const courseChaptersQueryOptions = courseJid =>
  queryOptions({
    queryKey: ['course', courseJid, 'chapters'],
    queryFn: () => courseChapterAPI.getChapters(getToken(), courseJid),
  });

export const setCourseChaptersMutationOptions = courseJid => ({
  mutationFn: data => courseChapterAPI.setChapters(getToken(), courseJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(courseChaptersQueryOptions(courseJid));
  },
});
