import { queryOptions } from '@tanstack/react-query';

import { submissionProgrammingAPI } from '../api/jerahmeel/submissionProgramming';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const submissionsQueryOptions = params => {
  const { username, page } = params || {};
  return queryOptions({
    queryKey: ['submissions', ...(params ? [params] : [])],
    queryFn: () => submissionProgrammingAPI.getSubmissions(getToken(), undefined, username, undefined, undefined, page),
  });
};

export const submissionWithSourceQueryOptions = (submissionId, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['submissions', submissionId, 'source', ...(params ? [params] : [])],
    queryFn: () => submissionProgrammingAPI.getSubmissionWithSource(getToken(), submissionId, language),
  });
};

export const regradeSubmissionMutationOptions = {
  mutationFn: submissionJid => submissionProgrammingAPI.regradeSubmission(getToken(), submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(submissionsQueryOptions());
  },
};

export const regradeSubmissionsMutationOptions = {
  mutationFn: ({ username } = {}) =>
    submissionProgrammingAPI.regradeSubmissions(getToken(), undefined, username, undefined, undefined),
  onSuccess: () => {
    queryClient.invalidateQueries(submissionsQueryOptions());
  },
};
