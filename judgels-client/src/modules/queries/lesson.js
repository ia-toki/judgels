import { queryOptions } from '@tanstack/react-query';

import { BadRequestError } from '../api/error';
import { LessonErrors, lessonAPI } from '../api/lesson';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const lessonsQueryOptions = params => {
  const { term, page } = params || {};
  return queryOptions({
    queryKey: ['lessons', ...(params ? [params] : [])],
    queryFn: () => lessonAPI.getLessons(getToken(), term, page),
  });
};

export const lessonQueryOptions = lessonJid =>
  queryOptions({
    queryKey: ['lesson', lessonJid],
    queryFn: () => lessonAPI.getLesson(getToken(), lessonJid),
  });

export const createLessonMutationOptions = {
  mutationFn: async data => {
    try {
      return await lessonAPI.createLesson(getToken(), data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === LessonErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(lessonsQueryOptions());
  },
};

export const updateLessonMutationOptions = lessonJid => ({
  mutationFn: async data => {
    try {
      return await lessonAPI.updateLesson(getToken(), lessonJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === LessonErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(lessonsQueryOptions());
    queryClient.invalidateQueries(lessonQueryOptions(lessonJid));
  },
});
