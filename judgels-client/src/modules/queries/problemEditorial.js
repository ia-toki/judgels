import { queryOptions } from '@tanstack/react-query';

import { problemEditorialAPI } from '../api/problemEditorial';
import { queryClient } from '../queryClient';
import { getToken } from '../session';
import { problemQueryOptions } from './problem';

// A write to a problem's files starts its local changes, which the problem itself reports,
// so each mutation invalidates the problem and, with it, everything inside it.
const invalidateProblem = problemJid => () => {
  queryClient.invalidateQueries(problemQueryOptions(problemJid));
};

export const problemEditorialQueryOptions = (problemJid, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['problem', problemJid, 'editorial', ...(params ? [params] : [])],
    queryFn: () => problemEditorialAPI.getEditorial(getToken(), problemJid, language),
  });
};

export const createProblemEditorialMutationOptions = problemJid => ({
  mutationFn: data => problemEditorialAPI.createEditorial(getToken(), problemJid, data),
  onSuccess: invalidateProblem(problemJid),
});

export const updateProblemEditorialMutationOptions = (problemJid, language) => ({
  mutationFn: editorial => problemEditorialAPI.updateEditorial(getToken(), problemJid, language, editorial),
  onSuccess: invalidateProblem(problemJid),
});

export const problemEditorialLanguagesQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'editorial', 'languages'],
    queryFn: () => problemEditorialAPI.getEditorialLanguages(getToken(), problemJid),
  });

export const addProblemEditorialLanguageMutationOptions = problemJid => ({
  mutationFn: language => problemEditorialAPI.addEditorialLanguage(getToken(), problemJid, language),
  onSuccess: invalidateProblem(problemJid),
});

export const enableProblemEditorialLanguageMutationOptions = problemJid => ({
  mutationFn: language => problemEditorialAPI.enableEditorialLanguage(getToken(), problemJid, language),
  onSuccess: invalidateProblem(problemJid),
});

export const disableProblemEditorialLanguageMutationOptions = problemJid => ({
  mutationFn: language => problemEditorialAPI.disableEditorialLanguage(getToken(), problemJid, language),
  onSuccess: invalidateProblem(problemJid),
});

export const makeProblemEditorialLanguageDefaultMutationOptions = problemJid => ({
  mutationFn: language => problemEditorialAPI.makeEditorialLanguageDefault(getToken(), problemJid, language),
  onSuccess: invalidateProblem(problemJid),
});

export const problemEditorialMediaFilesQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'editorial', 'media'],
    queryFn: () => problemEditorialAPI.getEditorialMediaFiles(getToken(), problemJid),
  });

export const uploadProblemEditorialMediaFileMutationOptions = problemJid => ({
  mutationFn: file => problemEditorialAPI.uploadEditorialMediaFile(getToken(), problemJid, file),
  onSuccess: invalidateProblem(problemJid),
});

export const uploadProblemEditorialMediaZipMutationOptions = problemJid => ({
  mutationFn: file => problemEditorialAPI.uploadEditorialMediaZip(getToken(), problemJid, file),
  onSuccess: invalidateProblem(problemJid),
});

export const deleteProblemEditorialMediaFileMutationOptions = problemJid => ({
  mutationFn: filename => problemEditorialAPI.deleteEditorialMediaFile(getToken(), problemJid, filename),
  onSuccess: invalidateProblem(problemJid),
});

export const deleteProblemEditorialMediaFilesMutationOptions = problemJid => ({
  mutationFn: () => problemEditorialAPI.deleteEditorialMediaFiles(getToken(), problemJid),
  onSuccess: invalidateProblem(problemJid),
});
