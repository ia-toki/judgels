import { queryOptions } from '@tanstack/react-query';

import { trainingProblemAPI } from '../api/trainingProblem';
import { getToken } from '../session';

export const trainingProblemsQueryOptions = params => {
  const { tags, page } = params || {};
  return queryOptions({
    queryKey: ['training-problems', ...(params ? [params] : [])],
    queryFn: () => trainingProblemAPI.getProblems(getToken(), tags, page),
  });
};
