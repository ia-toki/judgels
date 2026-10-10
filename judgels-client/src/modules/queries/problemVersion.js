import { queryOptions } from '@tanstack/react-query';
import { FORM_ERROR } from 'final-form';

import { BadRequestError } from '../api/error';
import { ProblemErrors } from '../api/problem';
import { problemVersionAPI } from '../api/problemVersion';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';
import { problemQueryOptions } from './problem';

// Committing, rebasing, discarding and restoring all change the files that the problem's other queries read,
// so each mutation invalidates the problem and, with it, everything inside it.
const invalidateProblem = problemJid => () => {
  queryClient.invalidateQueries(problemQueryOptions(problemJid));
};

const LOCAL_CHANGES_CONFLICT_ERROR =
  'Your local changes conflict with the newer committed changes. Save them elsewhere, discard them, then reapply them.';

export const problemVersionsQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'versions'],
    queryFn: () => problemVersionAPI.getVersions(getToken(), problemJid),
  });

export const restoreProblemVersionMutationOptions = problemJid => ({
  mutationFn: versionHash => problemVersionAPI.restoreVersion(getToken(), problemJid, versionHash),
  onSuccess: invalidateProblem(problemJid),
});

export const commitProblemVersionLocalChangesMutationOptions = problemJid => ({
  mutationFn: async data => {
    try {
      return await problemVersionAPI.commitVersionLocalChanges(getToken(), problemJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemErrors.VersionLocalChangesOutdated) {
        throw new SubmissionError({
          [FORM_ERROR]: 'There are newer committed changes. Rebase your local changes first.',
        });
      }
      if (error instanceof BadRequestError && error.message === ProblemErrors.VersionLocalChangesConflict) {
        throw new SubmissionError({ [FORM_ERROR]: LOCAL_CHANGES_CONFLICT_ERROR });
      }
      throw error;
    }
  },
  onSuccess: invalidateProblem(problemJid),
});

export const rebaseProblemVersionLocalChangesMutationOptions = problemJid => ({
  mutationFn: async () => {
    try {
      return await problemVersionAPI.rebaseVersionLocalChanges(getToken(), problemJid);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemErrors.VersionLocalChangesConflict) {
        throw new Error(LOCAL_CHANGES_CONFLICT_ERROR);
      }
      throw error;
    }
  },
  onSuccess: invalidateProblem(problemJid),
});

export const discardProblemVersionLocalChangesMutationOptions = problemJid => ({
  mutationFn: () => problemVersionAPI.discardVersionLocalChanges(getToken(), problemJid),
  onSuccess: invalidateProblem(problemJid),
});
