import { queryOptions } from '@tanstack/react-query';

import { submissionProgrammingAPI } from '../api/jerahmeel/submissionProgramming';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const chapterProgrammingSubmissionsQueryOptions = (chapterJid, params) => {
  const { problemAlias, username, page } = params || {};
  return queryOptions({
    queryKey: ['chapter', chapterJid, 'submissions', 'programming', ...(params ? [params] : [])],
    queryFn: () =>
      submissionProgrammingAPI.getSubmissions(getToken(), chapterJid, username, undefined, problemAlias, page),
  });
};

export const createChapterProgrammingSubmissionMutationOptions = (chapterJid, problemJid) => ({
  mutationFn: async data => {
    let sourceFiles = {};
    Object.keys(data.sourceFiles).forEach(key => {
      sourceFiles['sourceFiles.' + key] = data.sourceFiles[key];
    });

    return await submissionProgrammingAPI.createSubmission(
      getToken(),
      chapterJid,
      problemJid,
      data.gradingLanguage,
      sourceFiles
    );
  },
});

export const regradeChapterProgrammingSubmissionMutationOptions = chapterJid => ({
  mutationFn: submissionJid => submissionProgrammingAPI.regradeSubmission(getToken(), submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(chapterProgrammingSubmissionsQueryOptions(chapterJid));
  },
});

export const regradeChapterProgrammingSubmissionsMutationOptions = chapterJid => ({
  mutationFn: ({ problemAlias } = {}) =>
    submissionProgrammingAPI.regradeSubmissions(getToken(), chapterJid, undefined, undefined, problemAlias),
  onSuccess: () => {
    queryClient.invalidateQueries(chapterProgrammingSubmissionsQueryOptions(chapterJid));
  },
});
