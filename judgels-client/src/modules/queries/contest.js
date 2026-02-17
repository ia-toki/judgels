import { queryOptions } from '@tanstack/react-query';

import { BadRequestError } from '../api/error';
import { ContestErrors, contestAPI } from '../api/uriel/contest';
import { SubmissionError } from '../form/submissionError';
import { getToken } from '../session';

export const contestsQueryOptions = ({ name, page }) =>
  queryOptions({
    queryKey: ['contests', { name, page }],
    queryFn: () => contestAPI.getContests(getToken(), name, page),
  });

export const createContestMutationOptions = {
  mutationFn: async data => {
    const token = getToken();
    try {
      await contestAPI.createContest(token, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ContestErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
};

export const contestBySlugQueryOptions = contestSlug =>
  queryOptions({
    queryKey: ['contest-by-slug', contestSlug],
    queryFn: () => contestAPI.getContestBySlug(getToken(), contestSlug),
  });
