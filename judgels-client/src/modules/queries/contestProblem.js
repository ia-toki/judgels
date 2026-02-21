import { queryOptions } from '@tanstack/react-query';

import { ForbiddenError } from '../api/error';
import { ContestErrors } from '../api/uriel/contest';
import { contestProblemAPI } from '../api/uriel/contestProblem';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestProblemsQueryOptions = contestJid => {
  return queryOptions({
    queryKey: ['contest', contestJid, 'problems'],
    queryFn: () => contestProblemAPI.getProblems(getToken(), contestJid),
  });
};

export const setContestProblemsMutationOptions = contestJid => ({
  mutationFn: async data => {
    try {
      await contestProblemAPI.setProblems(getToken(), contestJid, data);
    } catch (error) {
      if (error instanceof ForbiddenError && error.message === ContestErrors.ProblemSlugsNotAllowed) {
        const unknownSlugs = error.args.slugs;
        throw new SubmissionError({ problems: 'Problems not found/allowed: ' + unknownSlugs });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(contestProblemsQueryOptions(contestJid));
  },
});

export const contestProgrammingProblemWorksheetQueryOptions = (contestJid, problemAlias, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'problems', problemAlias, 'worksheet', ...[params ? [params] : []]],
    queryFn: () => contestProblemAPI.getProgrammingProblemWorksheet(getToken(), contestJid, problemAlias, language),
  });
};

export const contestBundleProblemWorksheetQueryOptions = (contestJid, problemAlias, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'problems', problemAlias, 'worksheet', ...[params ? [params] : []]],
    queryFn: () => contestProblemAPI.getBundleProblemWorksheet(getToken(), contestJid, problemAlias, language),
  });
};
