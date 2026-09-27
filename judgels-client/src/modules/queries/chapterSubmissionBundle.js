import { queryOptions } from '@tanstack/react-query';

import { trainingSubmissionBundleAPI } from '../api/trainingSubmissionBundle';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const chapterBundleLatestSubmissionsQueryOptions = (chapterJid, problemAlias) =>
  queryOptions({
    queryKey: ['chapter', chapterJid, 'submissions', 'bundle', 'latest', problemAlias],
    queryFn: () => trainingSubmissionBundleAPI.getLatestSubmissions(getToken(), chapterJid, problemAlias),
  });

export const chapterBundleSubmissionSummaryQueryOptions = (chapterJid, params) => {
  const { problemAlias, language } = params || {};
  return queryOptions({
    queryKey: ['chapter', chapterJid, 'submissions', 'bundle', ...(params ? [params] : [])],
    queryFn: () =>
      trainingSubmissionBundleAPI.getSubmissionSummary(
        getToken(),
        chapterJid,
        undefined,
        undefined,
        problemAlias,
        language
      ),
  });
};

export const createChapterBundleItemSubmissionMutationOptions = (chapterJid, problemAlias) => ({
  mutationFn: async ({ problemJid, itemJid, answer }) => {
    await trainingSubmissionBundleAPI.createItemSubmission(getToken(), {
      containerJid: chapterJid,
      problemJid,
      itemJid,
      answer,
    });
  },
  onSuccess: () => {
    queryClient.invalidateQueries(chapterBundleLatestSubmissionsQueryOptions(chapterJid, problemAlias));
  },
});
