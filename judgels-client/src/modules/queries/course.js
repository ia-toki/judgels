import { queryOptions } from '@tanstack/react-query';

import { CourseErrors, courseAPI } from '../api/course';
import { courseChapterAPI } from '../api/courseChapter';
import { BadRequestError } from '../api/error';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const coursesQueryOptions = () =>
  queryOptions({
    queryKey: ['courses'],
    queryFn: () => courseAPI.getCourses(getToken()),
  });

export const courseBySlugQueryOptions = courseSlug =>
  queryOptions({
    queryKey: ['course-by-slug', courseSlug],
    queryFn: () => courseAPI.getCourseBySlug(getToken(), courseSlug),
  });

export const courseChaptersQueryOptions = courseJid =>
  queryOptions({
    queryKey: ['course', courseJid, 'chapters'],
    queryFn: () => courseChapterAPI.getChapters(getToken(), courseJid),
  });

export const courseChapterQueryOptions = (courseJid, chapterAlias) =>
  queryOptions({
    queryKey: ['course', courseJid, 'chapter', chapterAlias],
    queryFn: () => courseChapterAPI.getChapter(getToken(), courseJid, chapterAlias),
  });

export const createCourseMutationOptions = {
  mutationFn: async data => {
    try {
      await courseAPI.createCourse(getToken(), data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === CourseErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(coursesQueryOptions());
  },
};

export const updateCourseMutationOptions = courseJid => ({
  mutationFn: async data => {
    try {
      await courseAPI.updateCourse(getToken(), courseJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === CourseErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(coursesQueryOptions());
    queryClient.invalidateQueries({ queryKey: ['course-by-slug'] });
  },
});

export const setCourseChaptersMutationOptions = courseJid => ({
  mutationFn: data => courseChapterAPI.setChapters(getToken(), courseJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(courseChaptersQueryOptions(courseJid));
  },
});
