import { queryOptions } from '@tanstack/react-query';

import { contestItemSubmissionAPI } from '../api/contestItemSubmission';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestItemSubmissionsQueryOptions = (contestJid, params) => {
  const { username, problemAlias, page } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', 'bundle', ...(params ? [params] : [])],
    meta: { persist: false },
    queryFn: () => contestItemSubmissionAPI.getItemSubmissions(getToken(), contestJid, username, problemAlias, page),
  });
};

export const contestItemSubmissionSummaryQueryOptions = (contestJid, username, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', 'bundle', 'summary', username, ...(params ? [params] : [])],
    meta: { persist: false },
    queryFn: () => contestItemSubmissionAPI.getItemSubmissionSummary(getToken(), contestJid, username, language),
  });
};

export const contestLatestItemSubmissionsQueryOptions = (contestJid, problemAlias) => {
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', 'bundle', 'latest', problemAlias],
    meta: { persist: false },
    queryFn: () => contestItemSubmissionAPI.getLatestItemSubmissions(getToken(), contestJid, problemAlias),
  });
};

export const createContestItemSubmissionMutationOptions = (contestJid, problemAlias) => ({
  mutationFn: async ({ problemJid, itemJid, answer }) => {
    await contestItemSubmissionAPI.createItemSubmission(getToken(), {
      containerJid: contestJid,
      problemJid,
      itemJid,
      answer,
    });
  },
  onSuccess: () => {
    queryClient.invalidateQueries(contestLatestItemSubmissionsQueryOptions(contestJid, problemAlias));
  },
});

export const regradeContestItemSubmissionsMutationOptions = contestJid => ({
  mutationFn: ({ username, problemAlias } = {}) =>
    contestItemSubmissionAPI.regradeItemSubmissions(getToken(), contestJid, username, undefined, problemAlias),
  onSuccess: () => {
    queryClient.invalidateQueries(contestItemSubmissionsQueryOptions(contestJid));
    queryClient.invalidateQueries(contestItemSubmissionSummaryQueryOptions(contestJid));
  },
});
