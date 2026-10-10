import { queryOptions } from '@tanstack/react-query';

import { contestContestantAPI } from '../api/contestContestant';
import { queryClient } from '../queryClient';
import { getToken } from '../session';
import { contestWebConfigQueryOptions } from './contestWeb';

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

export const myContestantStateQueryOptions = contestJid =>
  queryOptions({
    queryKey: ['contest', contestJid, 'contestants', 'me', 'state'],
    queryFn: () => contestContestantAPI.getMyContestantState(getToken(), contestJid),
  });

export const contestApprovedContestantsCountQueryOptions = contestJid =>
  queryOptions({
    queryKey: ['contest', contestJid, 'contestants', 'approved', 'count'],
    queryFn: () => contestContestantAPI.getApprovedContestantsCount(getToken(), contestJid),
  });

export const contestApprovedContestantsQueryOptions = contestJid =>
  queryOptions({
    queryKey: ['contest', contestJid, 'contestants', 'approved'],
    queryFn: () => contestContestantAPI.getApprovedContestants(getToken(), contestJid),
  });

export const registerMyselfAsContestantMutationOptions = contestJid => ({
  mutationFn: () => contestContestantAPI.registerMyselfAsContestant(getToken(), contestJid),
  onSuccess: () => {
    queryClient.invalidateQueries(myContestantStateQueryOptions(contestJid));
    queryClient.invalidateQueries(contestApprovedContestantsCountQueryOptions(contestJid));
    queryClient.invalidateQueries(contestWebConfigQueryOptions(contestJid));
  },
});

export const unregisterMyselfAsContestantMutationOptions = contestJid => ({
  mutationFn: () => contestContestantAPI.unregisterMyselfAsContestant(getToken(), contestJid),
  onSuccess: () => {
    queryClient.invalidateQueries(myContestantStateQueryOptions(contestJid));
    queryClient.invalidateQueries(contestApprovedContestantsCountQueryOptions(contestJid));
    queryClient.invalidateQueries(contestWebConfigQueryOptions(contestJid));
  },
});
