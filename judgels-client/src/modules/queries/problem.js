import { queryOptions } from '@tanstack/react-query';

import { problemAPI } from '../api/jerahmeel/problem';
import { getToken } from '../session';

export const problemsQueryOptions = params => {
  const { tags, page } = params || {};
  return queryOptions({
    queryKey: ['problems', ...(params ? [params] : [])],
    queryFn: () => problemAPI.getProblems(getToken(), tags, page),
  });
};

export const problemTagsQueryOptions = () =>
  queryOptions({
    queryKey: ['problem-tags'],
    queryFn: () => problemAPI.getProblemTags(),
  });
