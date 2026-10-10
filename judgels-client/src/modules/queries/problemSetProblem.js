import { queryOptions } from '@tanstack/react-query';

import { ForbiddenError } from '../api/error';
import { ProblemSetErrors } from '../api/problemSet';
import { problemSetProblemAPI } from '../api/problemSetProblem';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemSetProblemsQueryOptions = problemSetJid =>
  queryOptions({
    queryKey: ['problem-set', problemSetJid, 'problems'],
    queryFn: () => problemSetProblemAPI.getProblems(getToken(), problemSetJid),
  });

export const problemSetProblemQueryOptions = (problemSetJid, problemAlias) =>
  queryOptions({
    queryKey: ['problem-set', problemSetJid, 'problem', problemAlias],
    queryFn: () => problemSetProblemAPI.getProblem(getToken(), problemSetJid, problemAlias),
  });

export const problemSetProblemWorksheetQueryOptions = (problemSetJid, problemAlias, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['problem-set', problemSetJid, 'problem', problemAlias, 'worksheet', ...(params ? [params] : [])],
    queryFn: () => problemSetProblemAPI.getProblemWorksheet(getToken(), problemSetJid, problemAlias, language),
  });
};

export const problemSetProblemReportQueryOptions = (problemSetJid, problemAlias) =>
  queryOptions({
    queryKey: ['problem-set', problemSetJid, 'problem', problemAlias, 'report'],
    queryFn: () => problemSetProblemAPI.getProblemReport(getToken(), problemSetJid, problemAlias),
  });

export const problemSetProblemEditorialQueryOptions = (problemSetJid, problemAlias, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['problem-set', problemSetJid, 'problem', problemAlias, 'editorial', ...(params ? [params] : [])],
    queryFn: () => problemSetProblemAPI.getProblemEditorial(problemSetJid, problemAlias, language),
  });
};

export const setProblemSetProblemsMutationOptions = problemSetJid => ({
  mutationFn: async data => {
    try {
      await problemSetProblemAPI.setProblems(getToken(), problemSetJid, data);
    } catch (error) {
      if (error instanceof ForbiddenError && error.message === ProblemSetErrors.ContestSlugsNotAllowed) {
        const unknownSlugs = error.args.contestSlugs;
        throw new SubmissionError({ problems: 'Contests not found/allowed: ' + unknownSlugs });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetProblemsQueryOptions(problemSetJid));
  },
});
