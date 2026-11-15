import { queryOptions } from '@tanstack/react-query';

import { contestSupervisorAPI } from '../api/contestSupervisor';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestSupervisorsQueryOptions = (contestJid, params) => {
  const { page } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'supervisors', ...(params ? [params] : [])],
    queryFn: () => contestSupervisorAPI.getSupervisors(getToken(), contestJid, page),
  });
};

export const addContestSupervisorsMutationOptions = contestJid => ({
  mutationFn: data => contestSupervisorAPI.addSupervisors(getToken(), contestJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(contestSupervisorsQueryOptions(contestJid));
  },
});

export const removeContestSupervisorsMutationOptions = contestJid => ({
  mutationFn: usernames => contestSupervisorAPI.removeSupervisors(getToken(), contestJid, usernames),
  onSuccess: () => {
    queryClient.invalidateQueries(contestSupervisorsQueryOptions(contestJid));
  },
});
