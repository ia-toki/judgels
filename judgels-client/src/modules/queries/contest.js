import { queryOptions } from '@tanstack/react-query';

import { isTLX } from '../../conf';
import { BadRequestError, NotFoundError, RemoteError } from '../api/error';
import { problemSetAPI } from '../api/jerahmeel/problemSet';
import { ContestErrors, contestAPI } from '../api/uriel/contest';
import { SubmissionError } from '../form/submissionError';
import { contestContestantsQueryOptions } from '../queries/contestContestant';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const activeContestsQueryOptions = () =>
  queryOptions({
    queryKey: ['contests', 'active'],
    queryFn: () => contestAPI.getActiveContests(getToken()),
  });

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

export const updateContestMutationOptions = (contestJid, contestSlug) => ({
  mutationFn: async data => {
    try {
      await contestAPI.updateContest(getToken(), contestJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ContestErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(contestBySlugQueryOptions(contestSlug));
  },
});

export const updateContestDescriptionMutationOptions = contestJid => ({
  mutationFn: description => contestAPI.updateContestDescription(getToken(), contestJid, { description }),
  onSuccess: () => {
    queryClient.invalidateQueries(contestDescriptionQueryOptions(contestJid));
  },
});

export const startVirtualContestMutationOptions = contestJid => ({
  mutationFn: () => contestAPI.startVirtualContest(getToken(), contestJid),
});

export const resetVirtualContestMutationOptions = contestJid => ({
  mutationFn: () => contestAPI.resetVirtualContest(getToken(), contestJid),
  onSuccess: () => {
    queryClient.invalidateQueries(contestContestantsQueryOptions(contestJid));
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
