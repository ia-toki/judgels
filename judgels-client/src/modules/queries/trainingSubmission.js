import { queryOptions } from '@tanstack/react-query';

import { getGradingLanguageEditorSubmissionFilename } from '../api/gradingLanguage';
import { trainingSubmissionAPI } from '../api/trainingSubmission';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

const submissionsQueryKey = filter => ['training', 'submissions', 'programming', ...(filter ? [filter] : [])];

export const trainingSubmissionsQueryOptions = params => {
  const { username, beforeId, afterId } = params || {};
  return queryOptions({
    queryKey: submissionsQueryKey(params),
    meta: { persist: false },
    queryFn: () =>
      trainingSubmissionAPI.getSubmissions(getToken(), undefined, username, undefined, undefined, beforeId, afterId),
  });
};

export const trainingSubmissionWithSourceByIdQueryOptions = (submissionId, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['training', 'submissions', 'programming', submissionId, 'source', ...(params ? [params] : [])],
    meta: { persist: false },
    queryFn: () => trainingSubmissionAPI.getSubmissionWithSourceById(getToken(), submissionId, language),
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

export const chapterSubmissionsQueryOptions = (chapterJid, params) => {
  const { problemAlias, username, beforeId, afterId } = params || {};
  return queryOptions({
    queryKey: submissionsQueryKey({ containerJid: chapterJid, ...params }),
    meta: { persist: false },
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

export const regradeChapterSubmissionsMutationOptions = chapterJid => ({
  mutationFn: ({ problemAlias } = {}) =>
    trainingSubmissionAPI.regradeSubmissions(getToken(), chapterJid, undefined, undefined, problemAlias),
  onSuccess: () => {
    queryClient.invalidateQueries(trainingSubmissionsQueryOptions());
  },
});

export const problemSetSubmissionsQueryOptions = (problemJid, params) => {
  const { username, beforeId, afterId } = params || {};
  return queryOptions({
    queryKey: submissionsQueryKey({ problemJid, ...params }),
    meta: { persist: false },
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

export const regradeProblemSetSubmissionsMutationOptions = problemJid => ({
  mutationFn: () => trainingSubmissionAPI.regradeSubmissions(getToken(), undefined, undefined, problemJid),
  onSuccess: () => {
    queryClient.invalidateQueries(trainingSubmissionsQueryOptions());
  },
});

export const profileSubmissionsQueryOptions = (username, params) => {
  const { beforeId, afterId } = params || {};
  return queryOptions({
    queryKey: submissionsQueryKey({ username, ...params }),
    meta: { persist: false },
    queryFn: () =>
      trainingSubmissionAPI.getSubmissions(getToken(), undefined, username, undefined, undefined, beforeId, afterId),
  });
};
