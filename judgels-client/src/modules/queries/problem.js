import { queryOptions } from '@tanstack/react-query';

import { BadRequestError } from '../api/error';
import { ProblemErrors, problemAPI } from '../api/problem';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemsQueryOptions = params => {
  const { term, tags, page } = params || {};
  return queryOptions({
    queryKey: ['problems', ...(params ? [params] : [])],
    queryFn: () => problemAPI.getProblems(getToken(), term, tags, page),
  });
};

export const problemQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid],
    queryFn: () => problemAPI.getProblem(getToken(), problemJid),
  });

export const createProblemMutationOptions = {
  mutationFn: async data => {
    try {
      return await problemAPI.createProblem(getToken(), data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(problemsQueryOptions());
  },
};

export const updateProblemMutationOptions = problemJid => ({
  mutationFn: async data => {
    try {
      return await problemAPI.updateProblem(getToken(), problemJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(problemsQueryOptions());
    queryClient.invalidateQueries(problemQueryOptions(problemJid));
  },
});
