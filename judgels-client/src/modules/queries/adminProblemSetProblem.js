import { queryOptions } from '@tanstack/react-query';

import { adminProblemSetProblemAPI } from '../api/admin/problemSetProblem';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemSetProblemsQueryOptions = problemSetJid =>
  queryOptions({
    queryKey: ['admin', 'problemset', problemSetJid, 'problems'],
    queryFn: () => adminProblemSetProblemAPI.getProblems(getToken(), problemSetJid),
  });

export const setProblemSetProblemsMutationOptions = problemSetJid => ({
  mutationFn: data => adminProblemSetProblemAPI.setProblems(getToken(), problemSetJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetProblemsQueryOptions(problemSetJid));
  },
});
