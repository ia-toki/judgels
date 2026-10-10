import { queryOptions } from '@tanstack/react-query';

import { problemTagAPI } from '../api/problemTag';
import { getToken } from '../session';

export const problemTagsQueryOptions = () =>
  queryOptions({
    queryKey: ['problems', 'tags'],
    queryFn: () => problemTagAPI.getTags(getToken()),
  });
