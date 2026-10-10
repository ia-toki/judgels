import { queryOptions } from '@tanstack/react-query';

import { trainingItemSubmissionAPI } from '../api/trainingItemSubmission';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const chapterLatestItemSubmissionsQueryOptions = (chapterJid, problemAlias) =>
  queryOptions({
    queryKey: ['chapter', chapterJid, 'submissions', 'bundle', 'latest', problemAlias],
    queryFn: () => trainingItemSubmissionAPI.getLatestSubmissions(getToken(), chapterJid, problemAlias),
  });

export const chapterItemSubmissionSummaryQueryOptions = (chapterJid, params) => {
  const { problemAlias, language } = params || {};
  return queryOptions({
    queryKey: ['chapter', chapterJid, 'submissions', 'bundle', ...(params ? [params] : [])],
    queryFn: () =>
      trainingItemSubmissionAPI.getSubmissionSummary(
        getToken(),
        chapterJid,
        undefined,
        undefined,
        problemAlias,
        language
      ),
  });
};

export const createChapterItemSubmissionMutationOptions = (chapterJid, problemAlias) => ({
  mutationFn: async ({ problemJid, itemJid, answer }) => {
    await trainingItemSubmissionAPI.createItemSubmission(getToken(), {
      containerJid: chapterJid,
      problemJid,
      itemJid,
      answer,
    });
  },
  onSuccess: () => {
    queryClient.invalidateQueries(chapterLatestItemSubmissionsQueryOptions(chapterJid, problemAlias));
  },
});
