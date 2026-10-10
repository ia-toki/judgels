import { queryOptions } from '@tanstack/react-query';

import { trainingSubmissionProgrammingAPI } from '../api/trainingSubmission';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const submissionsQueryOptions = params => {
  const { username, beforeId, afterId } = params || {};
  return queryOptions({
    queryKey: ['submissions', ...(params ? [params] : [])],
    queryFn: () =>
      trainingSubmissionProgrammingAPI.getSubmissions(
        getToken(),
        undefined,
        username,
        undefined,
        undefined,
        beforeId,
        afterId
      ),
  });
};

export const submissionWithSourceQueryOptions = (submissionId, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['submissions', submissionId, 'source', ...(params ? [params] : [])],
    queryFn: () => trainingSubmissionProgrammingAPI.getSubmissionWithSource(getToken(), submissionId, language),
  });
};

export const regradeSubmissionMutationOptions = {
  mutationFn: submissionJid => trainingSubmissionProgrammingAPI.regradeSubmission(getToken(), submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(submissionsQueryOptions());
  },
};

export const regradeSubmissionsMutationOptions = {
  mutationFn: ({ username } = {}) =>
    trainingSubmissionProgrammingAPI.regradeSubmissions(getToken(), undefined, username, undefined, undefined),
  onSuccess: () => {
    queryClient.invalidateQueries(submissionsQueryOptions());
  },
};
