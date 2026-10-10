import { queryOptions } from '@tanstack/react-query';

import { trainingSubmissionAPI } from '../api/trainingSubmission';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const trainingSubmissionsQueryOptions = params => {
  const { username, beforeId, afterId } = params || {};
  return queryOptions({
    queryKey: ['submissions', ...(params ? [params] : [])],
    queryFn: () =>
      trainingSubmissionAPI.getSubmissions(getToken(), undefined, username, undefined, undefined, beforeId, afterId),
  });
};

export const trainingSubmissionWithSourceQueryOptions = (submissionId, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['submissions', submissionId, 'source', ...(params ? [params] : [])],
    queryFn: () => trainingSubmissionAPI.getSubmissionWithSource(getToken(), submissionId, language),
  });
};

export const regradeTrainingSubmissionMutationOptions = {
  mutationFn: submissionJid => trainingSubmissionAPI.regradeSubmission(getToken(), submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(trainingSubmissionsQueryOptions());
  },
};

export const regradeTrainingSubmissionsMutationOptions = {
  mutationFn: ({ username } = {}) =>
    trainingSubmissionAPI.regradeSubmissions(getToken(), undefined, username, undefined, undefined),
  onSuccess: () => {
    queryClient.invalidateQueries(trainingSubmissionsQueryOptions());
  },
};
