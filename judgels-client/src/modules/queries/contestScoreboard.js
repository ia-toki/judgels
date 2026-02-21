import { queryOptions } from '@tanstack/react-query';

import { contestScoreboardAPI } from '../api/uriel/contestScoreboard';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestScoreboardQueryOptions = (contestJid, params) => {
  const { frozen, showClosedProblems, page } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'scoreboard', ...(params ? [params] : [])],
    queryFn: async () =>
      (await contestScoreboardAPI.getScoreboard(getToken(), contestJid, frozen, showClosedProblems, page)) ?? null,
  });
};

export const refreshContestScoreboardMutationOptions = contestJid => ({
  mutationFn: () => contestScoreboardAPI.refreshScoreboard(getToken(), contestJid),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['contest', contestJid, 'scoreboard'] });
  },
});
