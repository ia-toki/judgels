import { queryOptions } from '@tanstack/react-query';

import { problemSetProblemAPI } from '../api/problemSetProblem';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemSetProblemsQueryOptions = problemSetJid =>
  queryOptions({
    queryKey: ['problemset', problemSetJid, 'problems'],
    queryFn: () => problemSetProblemAPI.getProblems(getToken(), problemSetJid),
  });

export const setProblemSetProblemsMutationOptions = problemSetJid => ({
  mutationFn: data => problemSetProblemAPI.setProblems(getToken(), problemSetJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetProblemsQueryOptions(problemSetJid));
  },
});
