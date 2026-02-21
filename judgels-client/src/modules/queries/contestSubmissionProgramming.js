import { queryOptions } from '@tanstack/react-query';

import { NotFoundError } from '../api/error';
import { getGradingLanguageEditorSubmissionFilename } from '../api/gabriel/language';
import { contestSubmissionProgrammingAPI } from '../api/uriel/contestSubmissionProgramming';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestProgrammingSubmissionsQueryOptions = (contestJid, params) => {
  const { username, problemAlias, page } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', ...(params ? [params] : [])],
    queryFn: () => contestSubmissionProgrammingAPI.getSubmissions(getToken(), contestJid, username, problemAlias, page),
  });
};

export const contestUserProblemSubmissionsQueryOptions = (contestJid, userJid, problemJid) => {
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', userJid, problemJid],
    queryFn: () =>
      contestSubmissionProgrammingAPI.getUserProblemSubmissions(getToken(), contestJid, userJid, problemJid),
  });
};

export const contestSubmissionWithSourceQueryOptions = (contestJid, submissionId, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', submissionId, 'source', ...[params ? [params] : []]],
    queryFn: async () => {
      const submissionWithSource = await contestSubmissionProgrammingAPI.getSubmissionWithSource(
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

export const contestSubmissionInfoQueryOptions = (contestJid, userJid, problemJid) => {
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', userJid, problemJid, 'info'],
    queryFn: () => contestSubmissionProgrammingAPI.getSubmissionInfo(contestJid, userJid, problemJid),
  });
};

export const createProgrammingSubmissionMutationOptions = (contestJid, problemJid) => ({
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

    await contestSubmissionProgrammingAPI.createSubmission(
      getToken(),
      contestJid,
      problemJid,
      data.gradingLanguage,
      sources
    );
  },
});

export const regradeProgrammingSubmissionMutationOptions = contestJid => ({
  mutationFn: submissionJid => contestSubmissionProgrammingAPI.regradeSubmission(getToken(), submissionJid),
  onSuccess: () => {
    queryClient.invalidateQueries(contestProgrammingSubmissionsQueryOptions(contestJid));
  },
});

export const regradeProgrammingSubmissionsMutationOptions = contestJid => ({
  mutationFn: ({ username, problemAlias } = {}) =>
    contestSubmissionProgrammingAPI.regradeSubmissions(getToken(), contestJid, username, problemAlias),
  onSuccess: () => {
    queryClient.invalidateQueries(contestProgrammingSubmissionsQueryOptions(contestJid));
  },
});
