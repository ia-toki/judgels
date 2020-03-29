import { SubmissionError } from 'redux-form';

import { selectToken } from '../../../../modules/session/sessionSelectors';
import { BadRequestError } from '../../../../modules/api/error';
import { courseAPI, CourseCreateData, CourseUpdateData, CourseErrors } from '../../../../modules/api/jerahmeel/course';
import { courseChapterAPI, CourseChapter } from '../../../../modules/api/jerahmeel/courseChapter';
import * as toastActions from '../../../../modules/toast/toastActions';

export function createCourse(data: CourseCreateData) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await courseAPI.createCourse(token, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === CourseErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
    toastActions.showSuccessToast('Course created.');
  };
}

export function updateCourse(courseJid: string, data: CourseUpdateData) {
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
  };
}

export function getCourses() {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await courseAPI.getCourses(token);
  };
}

export function getChapters(courseJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await courseChapterAPI.getChapters(token, courseJid);
  };
}

export function setChapters(courseJid: string, data: CourseChapter[]) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await courseChapterAPI.setChapters(token, courseJid, data);

    toastActions.showSuccessToast('Course chapters updated.');
  };
}
