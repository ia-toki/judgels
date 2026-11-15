import { queryOptions } from '@tanstack/react-query';

import { contestManagerAPI } from '../api/contestManager';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestManagersQueryOptions = (contestJid, params) => {
  const { page } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'managers', ...(params ? [params] : [])],
    queryFn: () => contestManagerAPI.getManagers(getToken(), contestJid, page),
  });
};

export const addContestManagersMutationOptions = contestJid => ({
  mutationFn: usernames => contestManagerAPI.addManagers(getToken(), contestJid, usernames),
  onSuccess: () => {
    queryClient.invalidateQueries(contestManagersQueryOptions(contestJid));
  },
});

export const removeContestManagersMutationOptions = contestJid => ({
  mutationFn: usernames => contestManagerAPI.removeManagers(getToken(), contestJid, usernames),
  onSuccess: () => {
    queryClient.invalidateQueries(contestManagersQueryOptions(contestJid));
  },
});
