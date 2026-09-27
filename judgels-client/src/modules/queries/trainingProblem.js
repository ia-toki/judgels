import { queryOptions } from '@tanstack/react-query';

import { trainingProblemAPI } from '../api/trainingProblem';
import { getToken } from '../session';

export const problemsQueryOptions = params => {
  const { tags, page } = params || {};
  return queryOptions({
    queryKey: ['problems', ...(params ? [params] : [])],
    queryFn: () => trainingProblemAPI.getProblems(getToken(), tags, page),
  });
};

export const problemTagsQueryOptions = () =>
  queryOptions({
    queryKey: ['problem-tags'],
    queryFn: () => trainingProblemAPI.getProblemTags(),
  });
