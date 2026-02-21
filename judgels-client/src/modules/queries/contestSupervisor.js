import { queryOptions } from '@tanstack/react-query';

import { contestSupervisorAPI } from '../api/uriel/contestSupervisor';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestSupervisorsQueryOptions = (contestJid, params) => {
  const { page } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'supervisors', ...(params ? [params] : [])],
    queryFn: () => contestSupervisorAPI.getSupervisors(getToken(), contestJid, page),
  });
};

export const upsertContestSupervisorsMutationOptions = contestJid => ({
  mutationFn: data => contestSupervisorAPI.upsertSupervisors(getToken(), contestJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(contestSupervisorsQueryOptions(contestJid));
  },
});

export const deleteContestSupervisorsMutationOptions = contestJid => ({
  mutationFn: usernames => contestSupervisorAPI.deleteSupervisors(getToken(), contestJid, usernames),
  onSuccess: () => {
    queryClient.invalidateQueries(contestSupervisorsQueryOptions(contestJid));
  },
});
