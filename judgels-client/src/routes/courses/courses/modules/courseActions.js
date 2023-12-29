import { courseAPI } from '../../../../modules/api/jerahmeel/course';
import { selectToken } from '../../../../modules/session/sessionSelectors';
import { DelCourse, PutCourse } from './courseReducer';

export function getCourses() {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await courseAPI.getCourses(token);
  };
}

export function getCourseBySlug(courseSlug) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const course = await courseAPI.getCourseBySlug(token, courseSlug);
    dispatch(PutCourse(course));
    return course;
  };
}

export const clearCourse = DelCourse;
