import { queryOptions } from '@tanstack/react-query';

import { isTLX } from '../../conf';
import { BadRequestError, NotFoundError, RemoteError } from '../api/error';
import { ProblemSetErrors, problemSetAPI } from '../api/problemSet';
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
    queryClient.invalidateQueries({ queryKey: ['problem-set-by-slug'] });
  },
});

export const searchProblemSetQueryOptions = contestJid =>
  queryOptions({
    queryKey: ['contest', contestJid, 'problem-set'],
    queryFn: async () => {
      if (!isTLX()) {
        return null;
      }
      try {
        return await problemSetAPI.searchProblemSet(contestJid);
      } catch (error) {
        if (error instanceof NotFoundError || error instanceof RemoteError) {
          return null;
        }
        throw error;
      }
    },
  });
