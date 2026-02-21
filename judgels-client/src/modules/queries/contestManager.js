import { queryOptions } from '@tanstack/react-query';

import { contestManagerAPI } from '../api/uriel/contestManager';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestManagersQueryOptions = (contestJid, params) => {
  const { page } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'managers', ...(params ? [params] : [])],
    queryFn: () => contestManagerAPI.getManagers(getToken(), contestJid, page),
  });
};

export const upsertContestManagersMutationOptions = contestJid => ({
  mutationFn: usernames => contestManagerAPI.upsertManagers(getToken(), contestJid, usernames),
  onSuccess: () => {
    queryClient.invalidateQueries(contestManagersQueryOptions(contestJid));
  },
});

export const deleteContestManagersMutationOptions = contestJid => ({
  mutationFn: usernames => contestManagerAPI.deleteManagers(getToken(), contestJid, usernames),
  onSuccess: () => {
    queryClient.invalidateQueries(contestManagersQueryOptions(contestJid));
  },
});
