import { queryOptions } from '@tanstack/react-query';

import { getGradingLanguageEditorSubmissionFilename } from '../api/gradingLanguage';
import { trainingSubmissionAPI } from '../api/trainingSubmission';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemSetSubmissionsQueryOptions = (problemJid, params) => {
  const { username, beforeId, afterId } = params || {};
  return queryOptions({
    queryKey: ['problem-set', 'submissions', 'programming', problemJid, ...(params ? [params] : [])],
    queryFn: () =>
      trainingSubmissionAPI.getSubmissions(getToken(), undefined, username, problemJid, undefined, beforeId, afterId),
  });
};

export const createProblemSetSubmissionMutationOptions = (problemSetJid, problemJid) => ({
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

    await trainingSubmissionAPI.createSubmission(getToken(), problemSetJid, problemJid, data.gradingLanguage, sources);
  },
});

export const regradeProblemSetSubmissionMutationOptions = problemJid => ({
  mutationFn: submissionJid => trainingSubmissionAPI.regradeSubmission(getToken(), submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetSubmissionsQueryOptions(problemJid));
  },
});

export const regradeProblemSetSubmissionsMutationOptions = problemJid => ({
  mutationFn: () => trainingSubmissionAPI.regradeSubmissions(getToken(), undefined, undefined, problemJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetSubmissionsQueryOptions(problemJid));
  },
});
