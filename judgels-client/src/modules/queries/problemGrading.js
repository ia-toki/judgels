import { queryOptions } from '@tanstack/react-query';

import { problemGradingAPI } from '../api/problemGrading';
import { queryClient } from '../queryClient';
import { getToken } from '../session';
import { problemQueryOptions } from './problem';

// A write to a problem's files starts its local changes, which the problem itself reports,
// so each mutation invalidates the problem and, with it, everything inside it.
const invalidateProblem = problemJid => () => {
  queryClient.invalidateQueries(problemQueryOptions(problemJid));
};

export const updateProblemGradingEngineMutationOptions = problemJid => ({
  mutationFn: engine => problemGradingAPI.updateGradingEngine(getToken(), problemJid, { engine }),
  onSuccess: invalidateProblem(problemJid),
});

export const problemGradingConfigQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'grading', 'config'],
    queryFn: () => problemGradingAPI.getGradingConfig(getToken(), problemJid),
  });

export const updateProblemGradingConfigMutationOptions = problemJid => ({
  mutationFn: data => problemGradingAPI.updateGradingConfig(getToken(), problemJid, data),
  onSuccess: invalidateProblem(problemJid),
});

// This only proposes a config; nothing changes until the proposal is saved.
export const autoPopulateProblemGradingConfigMutationOptions = problemJid => ({
  mutationFn: () => problemGradingAPI.autoPopulateGradingConfig(getToken(), problemJid),
});

export const problemGradingTestDataFilesQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'grading', 'test-data'],
    queryFn: () => problemGradingAPI.getGradingTestDataFiles(getToken(), problemJid),
  });

export const uploadProblemGradingTestDataFileMutationOptions = problemJid => ({
  mutationFn: file => problemGradingAPI.uploadGradingTestDataFile(getToken(), problemJid, file),
  onSuccess: invalidateProblem(problemJid),
});

export const uploadProblemGradingTestDataZipMutationOptions = problemJid => ({
  mutationFn: file => problemGradingAPI.uploadGradingTestDataZip(getToken(), problemJid, file),
  onSuccess: invalidateProblem(problemJid),
});

export const deleteProblemGradingTestDataFileMutationOptions = problemJid => ({
  mutationFn: filename => problemGradingAPI.deleteGradingTestDataFile(getToken(), problemJid, filename),
  onSuccess: invalidateProblem(problemJid),
});

export const deleteProblemGradingTestDataFilesMutationOptions = problemJid => ({
  mutationFn: () => problemGradingAPI.deleteGradingTestDataFiles(getToken(), problemJid),
  onSuccess: invalidateProblem(problemJid),
});

export const problemGradingHelperFilesQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'grading', 'helpers'],
    queryFn: () => problemGradingAPI.getGradingHelperFiles(getToken(), problemJid),
  });

export const uploadProblemGradingHelperFileMutationOptions = problemJid => ({
  mutationFn: file => problemGradingAPI.uploadGradingHelperFile(getToken(), problemJid, file),
  onSuccess: invalidateProblem(problemJid),
});

export const uploadProblemGradingHelperZipMutationOptions = problemJid => ({
  mutationFn: file => problemGradingAPI.uploadGradingHelperZip(getToken(), problemJid, file),
  onSuccess: invalidateProblem(problemJid),
});

export const deleteProblemGradingHelperFileMutationOptions = problemJid => ({
  mutationFn: filename => problemGradingAPI.deleteGradingHelperFile(getToken(), problemJid, filename),
  onSuccess: invalidateProblem(problemJid),
});

export const deleteProblemGradingHelperFilesMutationOptions = problemJid => ({
  mutationFn: () => problemGradingAPI.deleteGradingHelperFiles(getToken(), problemJid),
  onSuccess: invalidateProblem(problemJid),
});

export const problemGradingLanguageRestrictionQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'grading', 'language-restriction'],
    queryFn: () => problemGradingAPI.getGradingLanguageRestriction(getToken(), problemJid),
  });

export const updateProblemGradingLanguageRestrictionMutationOptions = problemJid => ({
  mutationFn: languageRestriction =>
    problemGradingAPI.updateGradingLanguageRestriction(getToken(), problemJid, languageRestriction),
  onSuccess: invalidateProblem(problemJid),
});
