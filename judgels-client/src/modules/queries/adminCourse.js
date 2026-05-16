import { queryOptions } from '@tanstack/react-query';

import { adminCourseAPI } from '../api/admin/course';
import { CourseErrors } from '../api/course';
import { BadRequestError } from '../api/error';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const coursesQueryOptions = () =>
  queryOptions({
    queryKey: ['admin', 'courses'],
    queryFn: () => adminCourseAPI.getCourses(getToken()),
  });

export const courseBySlugQueryOptions = courseSlug =>
  queryOptions({
    queryKey: ['admin', 'course-by-slug', courseSlug],
    queryFn: () => adminCourseAPI.getCourseBySlug(getToken(), courseSlug),
  });

export const createCourseMutationOptions = {
  mutationFn: async data => {
    try {
      await adminCourseAPI.createCourse(getToken(), data);
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
      await adminCourseAPI.updateCourse(getToken(), courseJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === CourseErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(coursesQueryOptions());
    queryClient.invalidateQueries({ queryKey: ['admin', 'course-by-slug'] });
  },
});
