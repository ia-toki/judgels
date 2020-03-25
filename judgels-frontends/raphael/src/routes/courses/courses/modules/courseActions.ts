import { push } from 'connected-react-router';
import { SubmissionError } from 'redux-form';

import { DelCourse, PutCourse } from './courseReducer';
import { selectToken } from '../../../../modules/session/sessionSelectors';
import { BadRequestError } from '../../../../modules/api/error';
import { courseAPI, CourseCreateData, CourseUpdateData, CourseErrors } from '../../../../modules/api/jerahmeel/course';
import * as toastActions from '../../../../modules/toast/toastActions';

export function createCourse(data: CourseCreateData) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await courseAPI.createCourse(token, data);
    } catch (error) {
      console.log({ error });
      if (error instanceof BadRequestError && error.message === CourseErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
    dispatch(push(`/courses/${data.slug}`));
    toastActions.showSuccessToast('Course created.');
  };
}

export function updateCourse(courseJid: string, courseSlug: string, data: CourseUpdateData) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await courseAPI.updateCourse(token, courseJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === CourseErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
    toastActions.showSuccessToast('Course updated.');

    if (data.slug && data.slug !== courseSlug) {
      dispatch(push(`/courses/${data.slug}`));
    }
  };
}

export function getCourses() {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await courseAPI.getCourses(token);
  };
}

export function getCourseBySlug(courseSlug: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const course = await courseAPI.getCourseBySlug(token, courseSlug);
    dispatch(PutCourse.create(course));
    return course;
  };
}

export const clearCourse = DelCourse.create;
