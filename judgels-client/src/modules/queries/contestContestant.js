import { queryOptions } from '@tanstack/react-query';

import { contestContestantAPI } from '../api/uriel/contestContestant';
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

export const approvedContestantsCountQueryOptions = contestJid =>
  queryOptions({
    queryKey: ['contest', contestJid, 'contestants', 'approved', 'count'],
    queryFn: () => contestContestantAPI.getApprovedContestantsCount(getToken(), contestJid),
  });

export const approvedContestantsQueryOptions = contestJid =>
  queryOptions({
    queryKey: ['contest', contestJid, 'contestants', 'approved'],
    queryFn: () => contestContestantAPI.getApprovedContestants(getToken(), contestJid),
  });

export const registerMyselfMutationOptions = (contestJid, contestSlug) => ({
  mutationFn: () => contestContestantAPI.registerMyselfAsContestant(getToken(), contestJid),
  onSuccess: () => {
    queryClient.invalidateQueries(myContestantStateQueryOptions(contestJid));
    queryClient.invalidateQueries(approvedContestantsCountQueryOptions(contestJid));
    queryClient.invalidateQueries(contestWebConfigQueryOptions(contestSlug));
  },
});

export const unregisterMyselfMutationOptions = (contestJid, contestSlug) => ({
  mutationFn: () => contestContestantAPI.unregisterMyselfAsContestant(getToken(), contestJid),
  onSuccess: () => {
    queryClient.invalidateQueries(myContestantStateQueryOptions(contestJid));
    queryClient.invalidateQueries(approvedContestantsCountQueryOptions(contestJid));
    queryClient.invalidateQueries(contestWebConfigQueryOptions(contestSlug));
  },
});
