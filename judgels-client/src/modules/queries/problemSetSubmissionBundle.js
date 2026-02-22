import { queryOptions } from '@tanstack/react-query';

import { submissionBundleAPI } from '../api/jerahmeel/submissionBundle';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemSetBundleSubmissionsQueryOptions = (problemSetJid, params) => {
  const { username, problemAlias, page } = params || {};
  return queryOptions({
    queryKey: ['problem-set', problemSetJid, 'submissions', 'bundle', ...(params ? [params] : [])],
    queryFn: () => submissionBundleAPI.getSubmissions(getToken(), problemSetJid, username, problemAlias, page),
  });
};

export const problemSetBundleSubmissionSummaryQueryOptions = (problemSetJid, params) => {
  const { problemJid, username, language } = params || {};
  return queryOptions({
    queryKey: ['problem-set', problemSetJid, 'submissions', 'bundle', 'summary', ...(params ? [params] : [])],
    queryFn: () => submissionBundleAPI.getSubmissionSummary(getToken(), problemSetJid, problemJid, username, language),
  });
};

export const problemSetBundleLatestSubmissionsQueryOptions = (problemSetJid, problemAlias) =>
  queryOptions({
    queryKey: ['problem-set', problemSetJid, 'submissions', 'bundle', 'latest', problemAlias],
    queryFn: () => submissionBundleAPI.getLatestSubmissions(getToken(), problemSetJid, problemAlias),
  });

export const createProblemSetBundleItemSubmissionMutationOptions = (problemSetJid, problemAlias) => ({
  mutationFn: async ({ problemJid, itemJid, answer }) => {
    await submissionBundleAPI.createItemSubmission(getToken(), {
      containerJid: problemSetJid,
      problemJid,
      itemJid,
      answer,
    });
  },
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetBundleLatestSubmissionsQueryOptions(problemSetJid, problemAlias));
  },
});

export const regradeProblemSetBundleSubmissionMutationOptions = problemSetJid => ({
  mutationFn: submissionJid => submissionBundleAPI.regradeSubmission(getToken(), submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetBundleSubmissionsQueryOptions(problemSetJid));
    queryClient.invalidateQueries(problemSetBundleSubmissionSummaryQueryOptions(problemSetJid));
  },
});

export const regradeProblemSetBundleSubmissionsMutationOptions = problemSetJid => ({
  mutationFn: ({ userJid, problemJid } = {}) =>
    submissionBundleAPI.regradeSubmissions(getToken(), problemSetJid, userJid, problemJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetBundleSubmissionsQueryOptions(problemSetJid));
    queryClient.invalidateQueries(problemSetBundleSubmissionSummaryQueryOptions(problemSetJid));
  },
});
