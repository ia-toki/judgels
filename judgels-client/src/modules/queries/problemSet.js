import { queryOptions } from '@tanstack/react-query';

import { problemSetAPI } from '../api/jerahmeel/problemSet';
import { problemSetProblemAPI } from '../api/jerahmeel/problemSetProblem';

export const problemSetBySlugQueryOptions = problemSetSlug =>
  queryOptions({
    queryKey: ['problem-set-by-slug', problemSetSlug],
    queryFn: () => problemSetAPI.getProblemSetBySlug(problemSetSlug),
  });

export const problemSetProblemQueryOptions = (token, problemSetJid, problemAlias) =>
  queryOptions({
    queryKey: ['problem-set', problemSetJid, 'problem', problemAlias],
    queryFn: () => problemSetProblemAPI.getProblem(token, problemSetJid, problemAlias),
  });
