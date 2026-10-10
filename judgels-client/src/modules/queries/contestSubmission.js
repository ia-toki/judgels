import { queryOptions } from '@tanstack/react-query';

import { contestSubmissionAPI } from '../api/contestSubmission';
import { NotFoundError } from '../api/error';
import { getGradingLanguageEditorSubmissionFilename } from '../api/gradingLanguage';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestSubmissionsQueryOptions = (contestJid, params) => {
  const { username, problemAlias, page } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', 'programming', ...(params ? [params] : [])],
    meta: { persist: false },
    queryFn: () => contestSubmissionAPI.getSubmissions(getToken(), contestJid, username, problemAlias, page),
  });
};

export const contestUserProblemSubmissionsQueryOptions = (contestJid, userJid, problemJid) => {
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', 'programming', 'user-problem', userJid, problemJid],
    meta: { persist: false },
    queryFn: () => contestSubmissionAPI.getUserProblemSubmissions(getToken(), contestJid, userJid, problemJid),
  });
};

export const contestSubmissionWithSourceByIdQueryOptions = (contestJid, submissionId, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: [
      'contest',
      contestJid,
      'submissions',
      'programming',
      submissionId,
      'source',
      ...(params ? [params] : []),
    ],
    meta: { persist: false },
    queryFn: async () => {
      const submissionWithSource = await contestSubmissionAPI.getSubmissionWithSourceById(
        getToken(),
        submissionId,
        language
      );
      if (contestJid !== submissionWithSource.data.submission.containerJid) {
        throw new NotFoundError();
      }
      return submissionWithSource;
    },
  });
};

export const createContestSubmissionMutationOptions = (contestJid, problemJid) => ({
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

    await contestSubmissionAPI.createSubmission(getToken(), contestJid, problemJid, data.gradingLanguage, sources);
  },
});

export const regradeContestSubmissionMutationOptions = contestJid => ({
  mutationFn: submissionJid => contestSubmissionAPI.regradeSubmission(getToken(), submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(contestSubmissionsQueryOptions(contestJid));
  },
});

export const regradeContestSubmissionsMutationOptions = contestJid => ({
  mutationFn: ({ username, problemAlias } = {}) =>
    contestSubmissionAPI.regradeSubmissions(getToken(), contestJid, username, problemAlias),
  onSuccess: () => {
    queryClient.invalidateQueries(contestSubmissionsQueryOptions(contestJid));
  },
});
