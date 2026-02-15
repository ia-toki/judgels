import { queryOptions } from '@tanstack/react-query';

import { problemSetAPI } from '../api/jerahmeel/problemSet';
import { problemSetProblemAPI } from '../api/jerahmeel/problemSetProblem';
import { getToken } from '../session';

export const problemSetBySlugQueryOptions = problemSetSlug =>
  queryOptions({
    queryKey: ['problem-set-by-slug', problemSetSlug],
    queryFn: () => problemSetAPI.getProblemSetBySlug(problemSetSlug),
  });

export const problemSetProblemQueryOptions = (problemSetJid, problemAlias) =>
  queryOptions({
    queryKey: ['problem-set', problemSetJid, 'problem', problemAlias],
    queryFn: () => problemSetProblemAPI.getProblem(getToken(), problemSetJid, problemAlias),
  });
