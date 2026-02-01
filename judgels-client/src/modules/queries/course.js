import { queryOptions } from '@tanstack/react-query';

import { courseAPI } from '../api/jerahmeel/course';

export const courseBySlugQueryOptions = (token, courseSlug) =>
  queryOptions({
    queryKey: ['course-by-slug', courseSlug],
    queryFn: () => courseAPI.getCourseBySlug(token, courseSlug),
  });
