import { queryOptions } from '@tanstack/react-query';

import { lessonStatementAPI } from '../api/lessonStatement';
import { queryClient } from '../queryClient';
import { getToken } from '../session';
import { lessonQueryOptions } from './lesson';

// A write to a lesson's files starts its local changes, which the lesson itself reports,
// so each mutation invalidates the lesson and, with it, everything inside it.
const invalidateLesson = lessonJid => () => {
  queryClient.invalidateQueries(lessonQueryOptions(lessonJid));
};

export const lessonStatementQueryOptions = (lessonJid, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['lesson', lessonJid, 'statement', ...(params ? [params] : [])],
    queryFn: () => lessonStatementAPI.getStatement(getToken(), lessonJid, language),
  });
};

export const updateLessonStatementMutationOptions = (lessonJid, language) => ({
  mutationFn: statement => lessonStatementAPI.updateStatement(getToken(), lessonJid, language, statement),
  onSuccess: invalidateLesson(lessonJid),
});

export const lessonStatementLanguagesQueryOptions = lessonJid =>
  queryOptions({
    queryKey: ['lesson', lessonJid, 'statement', 'languages'],
    queryFn: () => lessonStatementAPI.getStatementLanguages(getToken(), lessonJid),
  });

export const addLessonStatementLanguageMutationOptions = lessonJid => ({
  mutationFn: language => lessonStatementAPI.addStatementLanguage(getToken(), lessonJid, language),
  onSuccess: invalidateLesson(lessonJid),
});

export const enableLessonStatementLanguageMutationOptions = lessonJid => ({
  mutationFn: language => lessonStatementAPI.enableStatementLanguage(getToken(), lessonJid, language),
  onSuccess: invalidateLesson(lessonJid),
});

export const disableLessonStatementLanguageMutationOptions = lessonJid => ({
  mutationFn: language => lessonStatementAPI.disableStatementLanguage(getToken(), lessonJid, language),
  onSuccess: invalidateLesson(lessonJid),
});

export const makeLessonStatementLanguageDefaultMutationOptions = lessonJid => ({
  mutationFn: language => lessonStatementAPI.makeStatementLanguageDefault(getToken(), lessonJid, language),
  onSuccess: invalidateLesson(lessonJid),
});

export const lessonStatementMediaFilesQueryOptions = lessonJid =>
  queryOptions({
    queryKey: ['lesson', lessonJid, 'statement', 'media'],
    queryFn: () => lessonStatementAPI.getStatementMediaFiles(getToken(), lessonJid),
  });

export const uploadLessonStatementMediaFileMutationOptions = lessonJid => ({
  mutationFn: file => lessonStatementAPI.uploadStatementMediaFile(getToken(), lessonJid, file),
  onSuccess: invalidateLesson(lessonJid),
});

export const uploadLessonStatementMediaZipMutationOptions = lessonJid => ({
  mutationFn: file => lessonStatementAPI.uploadStatementMediaZip(getToken(), lessonJid, file),
  onSuccess: invalidateLesson(lessonJid),
});

export const deleteLessonStatementMediaFileMutationOptions = lessonJid => ({
  mutationFn: filename => lessonStatementAPI.deleteStatementMediaFile(getToken(), lessonJid, filename),
  onSuccess: invalidateLesson(lessonJid),
});

export const deleteLessonStatementMediaFilesMutationOptions = lessonJid => ({
  mutationFn: () => lessonStatementAPI.deleteStatementMediaFiles(getToken(), lessonJid),
  onSuccess: invalidateLesson(lessonJid),
});
