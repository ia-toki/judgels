import { queryOptions } from '@tanstack/react-query';

import { adminProblemSetAPI } from '../api/admin/problemSet';
import { BadRequestError } from '../api/error';
import { ProblemSetErrors } from '../api/problemSet';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemSetsQueryOptions = params => {
  const { archiveSlug, name, page } = params || {};
  return queryOptions({
    queryKey: ['admin', 'problemsets', ...(params ? [params] : [])],
    queryFn: () => adminProblemSetAPI.getProblemSets(getToken(), archiveSlug, name, page),
  });
};

export const problemSetBySlugQueryOptions = problemSetSlug =>
  queryOptions({
    queryKey: ['admin', 'problemset-by-slug', problemSetSlug],
    queryFn: () => adminProblemSetAPI.getProblemSetBySlug(getToken(), problemSetSlug),
  });

export const createProblemSetMutationOptions = {
  mutationFn: async data => {
    try {
      await adminProblemSetAPI.createProblemSet(getToken(), data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemSetErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
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
      await adminProblemSetAPI.updateProblemSet(getToken(), problemSetJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemSetErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetsQueryOptions());
    queryClient.invalidateQueries({ queryKey: ['admin', 'problemset-by-slug'] });
  },
});
