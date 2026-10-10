import { queryOptions } from '@tanstack/react-query';

import { trainingProblemTagAPI } from '../api/trainingProblemTag';

export const trainingProblemTagsQueryOptions = () =>
  queryOptions({
    queryKey: ['training-problem-tags'],
    queryFn: () => trainingProblemTagAPI.getProblemTags(),
  });
