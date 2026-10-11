import { queryOptions } from '@tanstack/react-query';

import { getGradingLanguageEditorSubmissionFilename } from '../api/gradingLanguage';
import { problemSubmissionAPI } from '../api/problemSubmission';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemSubmissionsQueryOptions = (problemJid, params) => {
  const { page } = params || {};
  return queryOptions({
    queryKey: ['problem', problemJid, 'submissions', 'programming', ...(params ? [params] : [])],
    meta: { persist: false },
    queryFn: () => problemSubmissionAPI.getSubmissions(getToken(), problemJid, page),
  });
};

export const problemSubmissionConfigQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'submissions', 'programming', 'config'],
    meta: { persist: false },
    queryFn: () => problemSubmissionAPI.getSubmissionConfig(getToken(), problemJid),
  });

export const problemSubmissionWithSourceByIdQueryOptions = (problemJid, submissionId) =>
  queryOptions({
    queryKey: ['problem', problemJid, 'submissions', 'programming', submissionId, 'source'],
    meta: { persist: false },
    queryFn: () => problemSubmissionAPI.getSubmissionWithSourceById(getToken(), problemJid, submissionId),
  });

export const createProblemSubmissionMutationOptions = problemJid => ({
  mutationFn: async data => {
    let sources = {};
    Object.keys(data.sourceTexts ?? []).forEach(key => {
      sources['sourceFiles.' + key] = new File(
        [data.sourceTexts[key]],
        getGradingLanguageEditorSubmissionFilename(data.gradingLanguage),
        { type: 'text/plain' }
      );
    });
    Object.keys(data.sourceFiles ?? []).forEach(key => {
      sources['sourceFiles.' + key] = data.sourceFiles[key];
    });

    return await problemSubmissionAPI.createSubmission(getToken(), problemJid, data.gradingLanguage, sources);
  },
  onSuccess: () => {
    queryClient.invalidateQueries(problemSubmissionsQueryOptions(problemJid));
  },
});

export const regradeProblemSubmissionMutationOptions = problemJid => ({
  mutationFn: submissionJid => problemSubmissionAPI.regradeSubmission(getToken(), problemJid, submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSubmissionsQueryOptions(problemJid));
  },
});

export const regradeProblemSubmissionsMutationOptions = problemJid => ({
  mutationFn: () => problemSubmissionAPI.regradeSubmissions(getToken(), problemJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSubmissionsQueryOptions(problemJid));
  },
});
