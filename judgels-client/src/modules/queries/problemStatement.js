import { queryOptions } from '@tanstack/react-query';

import { problemStatementAPI } from '../api/problemStatement';
import { queryClient } from '../queryClient';
import { getToken } from '../session';
import { problemQueryOptions } from './problem';

// A write to a problem's files starts its local changes, which the problem itself reports,
// so each mutation invalidates the problem and, with it, everything inside it.
const invalidateProblem = problemJid => () => {
  queryClient.invalidateQueries(problemQueryOptions(problemJid));
};

export const problemStatementQueryOptions = (problemJid, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['problem', problemJid, 'statement', ...(params ? [params] : [])],
    queryFn: () => problemStatementAPI.getStatement(getToken(), problemJid, language),
  });
};

export const updateProblemStatementMutationOptions = (problemJid, language) => ({
  mutationFn: statement => problemStatementAPI.updateStatement(getToken(), problemJid, language, statement),
  onSuccess: invalidateProblem(problemJid),
});

export const problemStatementLanguagesQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'statement', 'languages'],
    queryFn: () => problemStatementAPI.getStatementLanguages(getToken(), problemJid),
  });

export const addProblemStatementLanguageMutationOptions = problemJid => ({
  mutationFn: language => problemStatementAPI.addStatementLanguage(getToken(), problemJid, language),
  onSuccess: invalidateProblem(problemJid),
});

export const enableProblemStatementLanguageMutationOptions = problemJid => ({
  mutationFn: language => problemStatementAPI.enableStatementLanguage(getToken(), problemJid, language),
  onSuccess: invalidateProblem(problemJid),
});

export const disableProblemStatementLanguageMutationOptions = problemJid => ({
  mutationFn: language => problemStatementAPI.disableStatementLanguage(getToken(), problemJid, language),
  onSuccess: invalidateProblem(problemJid),
});

export const makeProblemStatementLanguageDefaultMutationOptions = problemJid => ({
  mutationFn: language => problemStatementAPI.makeStatementLanguageDefault(getToken(), problemJid, language),
  onSuccess: invalidateProblem(problemJid),
});

export const problemStatementMediaFilesQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'statement', 'media'],
    queryFn: () => problemStatementAPI.getStatementMediaFiles(getToken(), problemJid),
  });

export const uploadProblemStatementMediaFileMutationOptions = problemJid => ({
  mutationFn: file => problemStatementAPI.uploadStatementMediaFile(getToken(), problemJid, file),
  onSuccess: invalidateProblem(problemJid),
});

export const uploadProblemStatementMediaZipMutationOptions = problemJid => ({
  mutationFn: file => problemStatementAPI.uploadStatementMediaZip(getToken(), problemJid, file),
  onSuccess: invalidateProblem(problemJid),
});

export const deleteProblemStatementMediaFileMutationOptions = problemJid => ({
  mutationFn: filename => problemStatementAPI.deleteStatementMediaFile(getToken(), problemJid, filename),
  onSuccess: invalidateProblem(problemJid),
});

export const deleteProblemStatementMediaFilesMutationOptions = problemJid => ({
  mutationFn: () => problemStatementAPI.deleteStatementMediaFiles(getToken(), problemJid),
  onSuccess: invalidateProblem(problemJid),
});
