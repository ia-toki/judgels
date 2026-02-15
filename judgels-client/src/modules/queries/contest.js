import { queryOptions } from '@tanstack/react-query';

import { contestAPI } from '../api/uriel/contest';
import { getToken } from '../session';

export const contestBySlugQueryOptions = contestSlug =>
  queryOptions({
    queryKey: ['contest-by-slug', contestSlug],
    queryFn: () => contestAPI.getContestBySlug(getToken(), contestSlug),
  });
