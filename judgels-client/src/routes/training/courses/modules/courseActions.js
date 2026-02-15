import { BadRequestError } from '../../../../modules/api/error';
import { CourseErrors, courseAPI } from '../../../../modules/api/jerahmeel/course';
import { courseChapterAPI } from '../../../../modules/api/jerahmeel/courseChapter';
import { SubmissionError } from '../../../../modules/form/submissionError';
import { getToken } from '../../../../modules/session';

import * as toastActions from '../../../../modules/toast/toastActions';

export async function createCourse(data) {
  const token = getToken();
  try {
    await courseAPI.createCourse(token, data);
  } catch (error) {
    if (error instanceof BadRequestError && error.message === CourseErrors.SlugAlreadyExists) {
      throw new SubmissionError({ slug: 'Slug already exists' });
    }
    throw error;
  }
  toastActions.showSuccessToast('Course created.');
}

export async function updateCourse(courseJid, data) {
  const token = getToken();
  try {
    await courseAPI.updateCourse(token, courseJid, data);
  } catch (error) {
    if (error instanceof BadRequestError && error.message === CourseErrors.SlugAlreadyExists) {
      throw new SubmissionError({ slug: 'Slug already exists' });
    }
    throw error;
  }
  toastActions.showSuccessToast('Course updated.');
}

export async function getCourses() {
  const token = getToken();
  return await courseAPI.getCourses(token);
}

export async function getChapters(courseJid) {
  const token = getToken();
  return await courseChapterAPI.getChapters(token, courseJid);
}

export async function setChapters(courseJid, data) {
  const token = getToken();
  await courseChapterAPI.setChapters(token, courseJid, data);

  toastActions.showSuccessToast('Course chapters updated.');
}
