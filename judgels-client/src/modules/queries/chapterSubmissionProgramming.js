import { queryOptions } from '@tanstack/react-query';

import { trainingSubmissionProgrammingAPI } from '../api/trainingSubmissionProgramming';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const chapterProgrammingSubmissionsQueryOptions = (chapterJid, params) => {
  const { problemAlias, username, beforeId, afterId } = params || {};
  return queryOptions({
    queryKey: ['chapter', chapterJid, 'submissions', 'programming', ...(params ? [params] : [])],
    queryFn: () =>
      trainingSubmissionProgrammingAPI.getSubmissions(
        getToken(),
        chapterJid,
        username,
        undefined,
        problemAlias,
        beforeId,
        afterId
      ),
  });
};

export const createChapterProgrammingSubmissionMutationOptions = (chapterJid, problemJid) => ({
  mutationFn: async data => {
    let sourceFiles = {};
    Object.keys(data.sourceFiles).forEach(key => {
      sourceFiles['sourceFiles.' + key] = data.sourceFiles[key];
    });

    return await trainingSubmissionProgrammingAPI.createSubmission(
      getToken(),
      chapterJid,
      problemJid,
      data.gradingLanguage,
      sourceFiles
    );
  },
});

export const regradeChapterProgrammingSubmissionMutationOptions = chapterJid => ({
  mutationFn: submissionJid => trainingSubmissionProgrammingAPI.regradeSubmission(getToken(), submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(chapterProgrammingSubmissionsQueryOptions(chapterJid));
  },
});

export const regradeChapterProgrammingSubmissionsMutationOptions = chapterJid => ({
  mutationFn: ({ problemAlias } = {}) =>
    trainingSubmissionProgrammingAPI.regradeSubmissions(getToken(), chapterJid, undefined, undefined, problemAlias),
  onSuccess: () => {
    queryClient.invalidateQueries(chapterProgrammingSubmissionsQueryOptions(chapterJid));
  },
});
