import { queryOptions } from '@tanstack/react-query';

import { trainingItemSubmissionAPI } from '../api/trainingItemSubmission';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const chapterLatestItemSubmissionsQueryOptions = (chapterJid, problemAlias) =>
  queryOptions({
    queryKey: ['chapter', chapterJid, 'submissions', 'bundle', 'latest', problemAlias],
    meta: { persist: false },
    queryFn: () => trainingItemSubmissionAPI.getLatestItemSubmissions(getToken(), chapterJid, problemAlias),
  });

export const chapterItemSubmissionSummaryQueryOptions = (chapterJid, params) => {
  const { problemAlias, language } = params || {};
  return queryOptions({
    queryKey: ['chapter', chapterJid, 'submissions', 'bundle', ...(params ? [params] : [])],
    meta: { persist: false },
    queryFn: () =>
      trainingItemSubmissionAPI.getItemSubmissionSummary(
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

export const problemSetItemSubmissionsQueryOptions = (problemSetJid, params) => {
  const { username, problemAlias, page } = params || {};
  return queryOptions({
    queryKey: ['problem-set', problemSetJid, 'submissions', 'bundle', ...(params ? [params] : [])],
    meta: { persist: false },
    queryFn: () =>
      trainingItemSubmissionAPI.getItemSubmissions(getToken(), problemSetJid, username, problemAlias, page),
  });
};

export const problemSetItemSubmissionSummaryQueryOptions = (problemSetJid, params) => {
  const { problemJid, username, language } = params || {};
  return queryOptions({
    queryKey: ['problem-set', problemSetJid, 'submissions', 'bundle', 'summary', ...(params ? [params] : [])],
    meta: { persist: false },
    queryFn: () =>
      trainingItemSubmissionAPI.getItemSubmissionSummary(getToken(), problemSetJid, problemJid, username, language),
  });
};

export const problemSetLatestItemSubmissionsQueryOptions = (problemSetJid, problemAlias) =>
  queryOptions({
    queryKey: ['problem-set', problemSetJid, 'submissions', 'bundle', 'latest', problemAlias],
    meta: { persist: false },
    queryFn: () => trainingItemSubmissionAPI.getLatestItemSubmissions(getToken(), problemSetJid, problemAlias),
  });

export const createProblemSetItemSubmissionMutationOptions = (problemSetJid, problemAlias) => ({
  mutationFn: async ({ problemJid, itemJid, answer }) => {
    await trainingItemSubmissionAPI.createItemSubmission(getToken(), {
      containerJid: problemSetJid,
      problemJid,
      itemJid,
      answer,
    });
  },
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetLatestItemSubmissionsQueryOptions(problemSetJid, problemAlias));
  },
});

export const regradeProblemSetItemSubmissionMutationOptions = problemSetJid => ({
  mutationFn: submissionJid => trainingItemSubmissionAPI.regradeItemSubmission(getToken(), submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetItemSubmissionsQueryOptions(problemSetJid));
    queryClient.invalidateQueries(problemSetItemSubmissionSummaryQueryOptions(problemSetJid));
  },
});

export const regradeProblemSetItemSubmissionsMutationOptions = problemSetJid => ({
  mutationFn: ({ userJid, problemJid } = {}) =>
    trainingItemSubmissionAPI.regradeItemSubmissions(getToken(), problemSetJid, userJid, problemJid),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSetItemSubmissionsQueryOptions(problemSetJid));
    queryClient.invalidateQueries(problemSetItemSubmissionSummaryQueryOptions(problemSetJid));
  },
});
