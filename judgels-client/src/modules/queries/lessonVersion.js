import { queryOptions } from '@tanstack/react-query';
import { FORM_ERROR } from 'final-form';

import { BadRequestError } from '../api/error';
import { LessonErrors } from '../api/lesson';
import { lessonVersionAPI } from '../api/lessonVersion';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';
import { lessonQueryOptions } from './lesson';

// Committing, rebasing, discarding and restoring all change the files that the lesson's other queries read,
// so each mutation invalidates the lesson and, with it, everything inside it.
const invalidateLesson = lessonJid => () => {
  queryClient.invalidateQueries(lessonQueryOptions(lessonJid));
};

const LOCAL_CHANGES_CONFLICT_ERROR =
  'Your local changes conflict with the newer committed changes. Save them elsewhere, discard them, then reapply them.';

export const lessonVersionsQueryOptions = lessonJid =>
  queryOptions({
    queryKey: ['lesson', lessonJid, 'versions'],
    queryFn: () => lessonVersionAPI.getVersions(getToken(), lessonJid),
  });

export const restoreLessonVersionMutationOptions = lessonJid => ({
  mutationFn: versionHash => lessonVersionAPI.restoreVersion(getToken(), lessonJid, versionHash),
  onSuccess: invalidateLesson(lessonJid),
});

export const commitLessonVersionLocalChangesMutationOptions = lessonJid => ({
  mutationFn: async data => {
    try {
      return await lessonVersionAPI.commitVersionLocalChanges(getToken(), lessonJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === LessonErrors.VersionLocalChangesOutdated) {
        throw new SubmissionError({
          [FORM_ERROR]: 'There are newer committed changes. Rebase your local changes first.',
        });
      }
      if (error instanceof BadRequestError && error.message === LessonErrors.VersionLocalChangesConflict) {
        throw new SubmissionError({ [FORM_ERROR]: LOCAL_CHANGES_CONFLICT_ERROR });
      }
      throw error;
    }
  },
  onSuccess: invalidateLesson(lessonJid),
});

export const rebaseLessonVersionLocalChangesMutationOptions = lessonJid => ({
  mutationFn: async () => {
    try {
      return await lessonVersionAPI.rebaseVersionLocalChanges(getToken(), lessonJid);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === LessonErrors.VersionLocalChangesConflict) {
        throw new Error(LOCAL_CHANGES_CONFLICT_ERROR);
      }
      throw error;
    }
  },
  onSuccess: invalidateLesson(lessonJid),
});

export const discardLessonVersionLocalChangesMutationOptions = lessonJid => ({
  mutationFn: () => lessonVersionAPI.discardVersionLocalChanges(getToken(), lessonJid),
  onSuccess: invalidateLesson(lessonJid),
});
