import { DelCourse, PutCourse } from './courseReducer';
import { selectToken } from '../../../../modules/session/sessionSelectors';
import { courseAPI } from '../../../../modules/api/jerahmeel/course';

export const courseActions = {
  getCourses: () => {
    return async (dispatch, getState) => {
      const token = selectToken(getState());
      return await courseAPI.getCourses(token);
    };
  },

  getCourseBySlug: (courseSlug: string) => {
    return async (dispatch, getState) => {
      const token = selectToken(getState());
      const course = await courseAPI.getCourseBySlug(token, courseSlug);
      dispatch(PutCourse.create(course));
      return course;
    };
  },

  clearCourse: DelCourse.create,
};
