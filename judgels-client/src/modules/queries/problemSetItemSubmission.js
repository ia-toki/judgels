import { queryOptions } from '@tanstack/react-query';

import { trainingItemSubmissionAPI } from '../api/trainingItemSubmission';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemSetItemSubmissionsQueryOptions = (problemSetJid, params) => {
  const { username, problemAlias, page } = params || {};
  return queryOptions({
    queryKey: ['problem-set', problemSetJid, 'submissions', 'bundle', ...(params ? [params] : [])],
    queryFn: () => trainingItemSubmissionAPI.getSubmissions(getToken(), problemSetJid, username, problemAlias, page),
  });
};

export const problemSetItemSubmissionSummaryQueryOptions = (problemSetJid, params) => {
  const { problemJid, username, language } = params || {};
  return queryOptions({
    queryKey: ['problem-set', problemSetJid, 'submissions', 'bundle', 'summary', ...(params ? [params] : [])],
    queryFn: () =>
      trainingItemSubmissionAPI.getSubmissionSummary(getToken(), problemSetJid, problemJid, username, language),
  });
};

export const problemSetLatestItemSubmissionsQueryOptions = (problemSetJid, problemAlias) =>
  queryOptions({
    queryKey: ['problem-set', problemSetJid, 'submissions', 'bundle', 'latest', problemAlias],
    queryFn: () => trainingItemSubmissionAPI.getLatestSubmissions(getToken(), problemSetJid, problemAlias),
  });

export const createProblemSetItemSubmissionMutationOptions = (problemSetJid, problemAlias) => ({
  mutationFn: async ({ problemJid, itemJid, answer }) => {
    await trainingItemSubmissionAPI.createItemSubmission(getToken(), {
      containerJid: problemSetJid,
      problemJid,
      itemJid,
      answer,
    });
  },
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetLatestItemSubmissionsQueryOptions(problemSetJid, problemAlias));
  },
});

export const regradeProblemSetItemSubmissionMutationOptions = problemSetJid => ({
  mutationFn: submissionJid => trainingItemSubmissionAPI.regradeSubmission(getToken(), submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetItemSubmissionsQueryOptions(problemSetJid));
    queryClient.invalidateQueries(problemSetItemSubmissionSummaryQueryOptions(problemSetJid));
  },
});

export const regradeProblemSetItemSubmissionsMutationOptions = problemSetJid => ({
  mutationFn: ({ userJid, problemJid } = {}) =>
    trainingItemSubmissionAPI.regradeSubmissions(getToken(), problemSetJid, userJid, problemJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetItemSubmissionsQueryOptions(problemSetJid));
    queryClient.invalidateQueries(problemSetItemSubmissionSummaryQueryOptions(problemSetJid));
  },
});
