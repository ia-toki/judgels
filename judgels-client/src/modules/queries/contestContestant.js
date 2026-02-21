import { queryOptions } from '@tanstack/react-query';

import { contestContestantAPI } from '../api/uriel/contestContestant';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestContestantsQueryOptions = (contestJid, params) => {
  const { page } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'contestants', ...(params ? [params] : [])],
    queryFn: () => contestContestantAPI.getContestants(getToken(), contestJid, page),
  });
};

export const upsertContestContestantsMutationOptions = contestJid => ({
  mutationFn: usernames => contestContestantAPI.upsertContestants(getToken(), contestJid, usernames),
  onSuccess: () => {
    queryClient.invalidateQueries(contestContestantsQueryOptions(contestJid));
  },
});

export const deleteContestContestantsMutationOptions = contestJid => ({
  mutationFn: usernames => contestContestantAPI.deleteContestants(getToken(), contestJid, usernames),
  onSuccess: () => {
    queryClient.invalidateQueries(contestContestantsQueryOptions(contestJid));
  },
});
