import { queryOptions } from '@tanstack/react-query';

import { getGradingLanguageEditorSubmissionFilename } from '../api/gabriel/language';
import { submissionProgrammingAPI } from '../api/jerahmeel/submissionProgramming';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemSetProgrammingSubmissionsQueryOptions = (problemJid, params) => {
  const { username, page } = params || {};
  return queryOptions({
    queryKey: ['problem-set', 'submissions', 'programming', problemJid, ...(params ? [params] : [])],
    queryFn: () =>
      submissionProgrammingAPI.getSubmissions(getToken(), undefined, username, problemJid, undefined, page),
  });
};

export const createProblemSetProgrammingSubmissionMutationOptions = (problemSetJid, problemJid) => ({
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

    await submissionProgrammingAPI.createSubmission(
      getToken(),
      problemSetJid,
      problemJid,
      data.gradingLanguage,
      sources
    );
  },
});

export const regradeProblemSetProgrammingSubmissionMutationOptions = problemJid => ({
  mutationFn: submissionJid => submissionProgrammingAPI.regradeSubmission(getToken(), submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetProgrammingSubmissionsQueryOptions(problemJid));
  },
});

export const regradeProblemSetProgrammingSubmissionsMutationOptions = problemJid => ({
  mutationFn: () => submissionProgrammingAPI.regradeSubmissions(getToken(), undefined, undefined, problemJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetProgrammingSubmissionsQueryOptions(problemJid));
  },
});
