import { queryOptions } from '@tanstack/react-query';

import { NotFoundError } from '../api/error';
import { getGradingLanguageEditorSubmissionFilename } from '../api/gabriel/language';
import { contestSubmissionProgrammingAPI } from '../api/uriel/contestSubmissionProgramming';
import { getToken } from '../session';

export const contestUserProblemSubmissionsQueryOptions = (contestJid, userJid, problemJid) => {
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', 'user-problem', userJid, problemJid],
    queryFn: () =>
      contestSubmissionProgrammingAPI.getUserProblemSubmissions(getToken(), contestJid, userJid, problemJid),
  });
};

export const contestSubmissionWithSourceQueryOptions = (contestJid, submissionId, language) => {
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', submissionId, 'source', language],
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
    queryKey: ['contest', contestJid, 'submissions', 'info', userJid, problemJid],
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
