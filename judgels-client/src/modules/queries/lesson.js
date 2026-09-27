import { queryOptions } from '@tanstack/react-query';

import { lessonAPI } from '../api/lesson';
import { getToken } from '../session';

export const lessonsQueryOptions = params => {
  const { term, page } = params || {};
  return queryOptions({
    queryKey: ['lessons', ...(params ? [params] : [])],
    queryFn: () => lessonAPI.getLessons(getToken(), term, page),
  });
};
