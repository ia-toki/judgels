import { queryOptions } from '@tanstack/react-query';

import { trainingSubmissionAPI } from '../api/trainingSubmission';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const chapterSubmissionsQueryOptions = (chapterJid, params) => {
  const { problemAlias, username, beforeId, afterId } = params || {};
  return queryOptions({
    queryKey: ['chapter', chapterJid, 'submissions', 'programming', ...(params ? [params] : [])],
    queryFn: () =>
      trainingSubmissionAPI.getSubmissions(
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

export const createChapterSubmissionMutationOptions = (chapterJid, problemJid) => ({
  mutationFn: async data => {
    let sourceFiles = {};
    Object.keys(data.sourceFiles).forEach(key => {
      sourceFiles['sourceFiles.' + key] = data.sourceFiles[key];
    });

    return await trainingSubmissionAPI.createSubmission(
      getToken(),
      chapterJid,
      problemJid,
      data.gradingLanguage,
      sourceFiles
    );
  },
});

export const regradeChapterSubmissionMutationOptions = chapterJid => ({
  mutationFn: submissionJid => trainingSubmissionAPI.regradeSubmission(getToken(), submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(chapterSubmissionsQueryOptions(chapterJid));
  },
});

export const regradeChapterSubmissionsMutationOptions = chapterJid => ({
  mutationFn: ({ problemAlias } = {}) =>
    trainingSubmissionAPI.regradeSubmissions(getToken(), chapterJid, undefined, undefined, problemAlias),
  onSuccess: () => {
    queryClient.invalidateQueries(chapterSubmissionsQueryOptions(chapterJid));
  },
});
