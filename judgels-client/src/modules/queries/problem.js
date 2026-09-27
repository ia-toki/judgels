import { queryOptions } from '@tanstack/react-query';

import { problemAPI } from '../api/problem';
import { getToken } from '../session';

export const problemsQueryOptions = params => {
  const { term, tags, page } = params || {};
  return queryOptions({
    queryKey: ['problems', ...(params ? [params] : [])],
    queryFn: () => problemAPI.getProblems(getToken(), term, tags, page),
  });
};
