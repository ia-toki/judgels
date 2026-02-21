import { queryOptions } from '@tanstack/react-query';

import { BadRequestError } from '../api/error';
import { ContestErrors, contestAPI } from '../api/uriel/contest';
import { SubmissionError } from '../form/submissionError';
import { contestContestantsQueryOptions } from '../queries/contestContestant';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestsQueryOptions = params => {
  const { name, page } = params || {};
  return queryOptions({
    queryKey: ['contests', ...[params ? [params] : []]],
    queryFn: () => contestAPI.getContests(getToken(), name, page),
  });
};

export const createContestMutationOptions = {
  mutationFn: async data => {
    try {
      await contestAPI.createContest(getToken(), data);
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
};

export const contestBySlugQueryOptions = contestSlug =>
  queryOptions({
    queryKey: ['contest-by-slug', contestSlug],
    queryFn: () => contestAPI.getContestBySlug(getToken(), contestSlug),
  });

export const contestDescriptionQueryOptions = contestJid =>
  queryOptions({
    queryKey: ['contest', contestJid, 'description'],
    queryFn: () => contestAPI.getContestDescription(getToken(), contestJid),
  });

export const resetVirtualContestMutationOptions = contestJid => ({
  mutationFn: () => contestAPI.resetVirtualContest(getToken(), contestJid),
  onSuccess: () => {
    queryClient.invalidateQueries(contestContestantsQueryOptions(contestJid));
  },
});
