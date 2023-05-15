import { selectToken } from '../../../../modules/session/sessionSelectors';
import { SubmissionError } from '../../../../modules/form/submissionError';
import { BadRequestError } from '../../../../modules/api/error';
import { courseAPI, CourseErrors } from '../../../../modules/api/jerahmeel/course';
import { courseChapterAPI } from '../../../../modules/api/jerahmeel/courseChapter';
import * as toastActions from '../../../../modules/toast/toastActions';

export function createCourse(data) {
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

export function updateCourse(courseJid, data) {
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

export function getChapters(courseJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await courseChapterAPI.getChapters(token, courseJid);
  };
}

export function setChapters(courseJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await courseChapterAPI.setChapters(token, courseJid, data);

    toastActions.showSuccessToast('Course chapters updated.');
  };
}
