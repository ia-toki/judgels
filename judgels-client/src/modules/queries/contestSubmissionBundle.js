import { queryOptions } from '@tanstack/react-query';

import { contestSubmissionBundleAPI } from '../api/uriel/contestSubmissionBundle';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestBundleSubmissionsQueryOptions = (contestJid, params) => {
  const { username, problemAlias, page } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', 'bundle', ...(params ? [params] : [])],
    queryFn: () => contestSubmissionBundleAPI.getSubmissions(getToken(), contestJid, username, problemAlias, page),
  });
};

export const contestBundleSubmissionSummaryQueryOptions = (contestJid, username, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', 'bundle', 'summary', username, ...(params ? [params] : [])],
    queryFn: () => contestSubmissionBundleAPI.getSubmissionSummary(getToken(), contestJid, username, language),
  });
};

export const contestBundleLatestSubmissionsQueryOptions = (contestJid, problemAlias) => {
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', 'bundle', problemAlias],
    queryFn: () => contestSubmissionBundleAPI.getLatestSubmissions(getToken(), contestJid, problemAlias),
  });
};

export const createBundleItemSubmissionMutationOptions = (contestJid, problemAlias) => ({
  mutationFn: async ({ problemJid, itemJid, answer }) => {
    await contestSubmissionBundleAPI.createItemSubmission(getToken(), {
      containerJid: contestJid,
      problemJid,
      itemJid,
      answer,
    });
  },
  onSuccess: () => {
    queryClient.invalidateQueries(contestBundleLatestSubmissionsQueryOptions(contestJid, problemAlias));
  },
});

export const regradeBundleSubmissionsMutationOptions = contestJid => ({
  mutationFn: ({ username, problemAlias } = {}) =>
    contestSubmissionBundleAPI.regradeSubmissions(getToken(), contestJid, username, undefined, problemAlias),
  onSuccess: () => {
    queryClient.invalidateQueries(contestBundleSubmissionsQueryOptions(contestJid));
    queryClient.invalidateQueries(contestBundleSubmissionSummaryQueryOptions(contestJid));
  },
});
