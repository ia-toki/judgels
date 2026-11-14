import { queryOptions } from '@tanstack/react-query';

import { trainingProblemTagAPI } from '../api/trainingProblemTag';

export const trainingProblemTagsQueryOptions = () =>
  queryOptions({
    queryKey: ['training', 'problems', 'tags'],
    queryFn: () => trainingProblemTagAPI.getProblemTags(),
  });
