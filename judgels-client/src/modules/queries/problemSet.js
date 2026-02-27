import { queryOptions } from '@tanstack/react-query';

import { BadRequestError, ForbiddenError } from '../api/error';
import { ProblemSetErrors, problemSetAPI } from '../api/jerahmeel/problemSet';
import { problemSetProblemAPI } from '../api/jerahmeel/problemSetProblem';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemSetsQueryOptions = params => {
  const { archiveSlug, name, page } = params || {};
  return queryOptions({
    queryKey: ['problem-sets', ...(params ? [params] : [])],
    queryFn: () => problemSetAPI.getProblemSets(getToken(), archiveSlug, name, page),
  });
};

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

export const problemSetProblemsQueryOptions = problemSetJid =>
  queryOptions({
    queryKey: ['problem-set', problemSetJid, 'problems'],
    queryFn: () => problemSetProblemAPI.getProblems(getToken(), problemSetJid),
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

export const createProblemSetMutationOptions = {
  mutationFn: async data => {
    try {
      await problemSetAPI.createProblemSet(getToken(), data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemSetErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      if (error instanceof BadRequestError && error.message === ProblemSetErrors.ArchiveSlugNotFound) {
        throw new SubmissionError({ archiveSlug: 'Archive slug not found' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetsQueryOptions());
  },
};

export const updateProblemSetMutationOptions = problemSetJid => ({
  mutationFn: async data => {
    try {
      await problemSetAPI.updateProblemSet(getToken(), problemSetJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemSetErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      if (error instanceof BadRequestError && error.message === ProblemSetErrors.ArchiveSlugNotFound) {
        throw new SubmissionError({ archiveSlug: 'Archive slug not found' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetsQueryOptions());
  },
});

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
