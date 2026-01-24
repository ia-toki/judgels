import { queryOptions } from '@tanstack/react-query';

import { contestAPI } from '../api/uriel/contest';

export const contestBySlugQueryOptions = (token, contestSlug) =>
  queryOptions({
    queryKey: ['contest-by-slug', contestSlug],
    queryFn: () => contestAPI.getContestBySlug(token, contestSlug),
  });
