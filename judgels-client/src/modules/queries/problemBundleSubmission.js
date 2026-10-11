import { queryOptions } from '@tanstack/react-query';

import { problemBundleSubmissionAPI } from '../api/problemBundleSubmission';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemBundleSubmissionsQueryOptions = (problemJid, params) => {
  const { page } = params || {};
  return queryOptions({
    queryKey: ['problem', problemJid, 'submissions', 'bundle', ...(params ? [params] : [])],
    meta: { persist: false },
    queryFn: () => problemBundleSubmissionAPI.getBundleSubmissions(getToken(), problemJid, page),
  });
};

export const problemBundleSubmissionWithAnswerByIdQueryOptions = (problemJid, submissionId) =>
  queryOptions({
    queryKey: ['problem', problemJid, 'submissions', 'bundle', submissionId, 'answer'],
    meta: { persist: false },
    queryFn: () => problemBundleSubmissionAPI.getBundleSubmissionWithAnswerById(getToken(), problemJid, submissionId),
  });

export const createProblemBundleSubmissionMutationOptions = problemJid => ({
  mutationFn: data => problemBundleSubmissionAPI.createBundleSubmission(getToken(), problemJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(problemBundleSubmissionsQueryOptions(problemJid));
  },
});

export const regradeProblemBundleSubmissionMutationOptions = problemJid => ({
  mutationFn: submissionJid =>
    problemBundleSubmissionAPI.regradeBundleSubmission(getToken(), problemJid, submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemBundleSubmissionsQueryOptions(problemJid));
  },
});

export const regradeProblemBundleSubmissionsMutationOptions = problemJid => ({
  mutationFn: () => problemBundleSubmissionAPI.regradeBundleSubmissions(getToken(), problemJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemBundleSubmissionsQueryOptions(problemJid));
  },
});
