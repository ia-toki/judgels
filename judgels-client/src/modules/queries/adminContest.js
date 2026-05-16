import { queryOptions } from '@tanstack/react-query';

import { adminContestAPI } from '../api/admin/contest';
import { ContestErrors } from '../api/contest';
import { BadRequestError } from '../api/error';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestsQueryOptions = params => {
  const { name, page } = params || {};
  return queryOptions({
    queryKey: ['admin', 'contests', ...[params ? [params] : []]],
    queryFn: () => adminContestAPI.getContests(getToken(), name, page),
  });
};

export const createContestMutationOptions = () => ({
  mutationFn: async data => {
    try {
      await adminContestAPI.createContest(getToken(), data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ContestErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(contestsQueryOptions());
  },
});
